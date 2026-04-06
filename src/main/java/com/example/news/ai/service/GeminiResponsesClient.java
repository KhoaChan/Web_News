package com.example.news.ai.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.news.ai.config.GeminiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiResponsesClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GeminiProperties geminiProperties;

    public boolean isConfigured() {
        return geminiProperties.isConfigured();
    }

    public String createStructuredResponse(List<Map<String, Object>> input, Map<String, Object> schema)
            throws IOException, InterruptedException {
        String systemInstruction = null;
        List<Map<String, Object>> contents = new ArrayList<>();
        for (Map<String, Object> message : input) {
            String role = Objects.toString(message.get("role"), "user");
            String text = Objects.toString(message.get("content"), "");
            if (text.isBlank()) {
                continue;
            }
            if ("developer".equalsIgnoreCase(role) || "system".equalsIgnoreCase(role)) {
                systemInstruction = appendInstruction(systemInstruction, text);
                continue;
            }
            contents.add(Map.of(
                    "role", "assistant".equalsIgnoreCase(role) ? "model" : "user",
                    "parts", List.of(Map.of("text", text))));
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("contents", contents);
        if (StringUtils.hasText(systemInstruction)) {
            payload.put("systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", systemInstruction))));
        }
        payload.put("generationConfig", Map.of(
                "temperature", 0.4,
                "responseMimeType", "application/json",
                "responseJsonSchema", schema));

        IOException lastException = null;
        for (String modelName : candidateModels()) {
            try {
                return sendStructuredRequest(modelName, payload);
            } catch (IOException exception) {
                lastException = exception;
                if (!isModelAvailabilityError(exception)) {
                    throw exception;
                }
                log.warn("Gemini model {} is not available, trying fallback model. Cause: {}", modelName, exception.getMessage());
            }
        }
        throw lastException == null
                ? new IOException("Khong the ket noi Gemini voi bat ky model fallback nao.")
                : lastException;
    }

    private String sendStructuredRequest(String modelName, Map<String, Object> payload)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geminiProperties.resolvedBaseUrl()
                        + "/models/" + modelName
                        + ":generateContent"))
                .timeout(Duration.ofSeconds(60))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-goog-api-key", geminiProperties.apiKey())
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(buildGeminiApiError(response));
        }

        JsonNode root = objectMapper.readTree(response.body());
        String text = readText(root);
        if (!StringUtils.hasText(text)) {
            String blockReason = root.path("promptFeedback").path("blockReason").asText("");
            JsonNode candidate = root.path("candidates").path(0);
            String finishReason = candidate.path("finishReason").asText("");
            if (StringUtils.hasText(blockReason)) {
                throw new IOException("Gemini blocked the prompt: " + blockReason);
            }
            if (StringUtils.hasText(finishReason)) {
                throw new IOException("Gemini finished without text: " + finishReason);
            }
            throw new IOException("Gemini response did not contain text");
        }
        return stripMarkdownFence(text);
    }

    private List<String> candidateModels() {
        Set<String> models = new LinkedHashSet<>();
        models.add(geminiProperties.resolvedModel());
        models.add("gemini-2.5-flash");
        models.add("gemini-2.5-flash-lite");
        models.add("gemini-2.0-flash");
        return models.stream()
                .filter(StringUtils::hasText)
                .toList();
    }

    private String appendInstruction(String current, String next) {
        if (!StringUtils.hasText(current)) {
            return next;
        }
        return current + "\n\n" + next;
    }

    private boolean isModelAvailabilityError(IOException exception) {
        String message = exception.getMessage();
        if (!StringUtils.hasText(message)) {
            return false;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("models/")
                || normalized.contains("not found")
                || normalized.contains("is not found")
                || normalized.contains("not supported")
                || normalized.contains("unsupported model");
    }

    private String buildGeminiApiError(HttpResponse<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode error = root.path("error");
            StringJoiner joiner = new StringJoiner(" - ");
            joiner.add("Gemini API error: " + response.statusCode());
            if (StringUtils.hasText(error.path("status").asText())) {
                joiner.add(error.path("status").asText());
            }
            if (StringUtils.hasText(error.path("message").asText())) {
                joiner.add(error.path("message").asText());
            } else {
                joiner.add(response.body());
            }
            return joiner.toString();
        } catch (Exception ignored) {
            return "Gemini API error: " + response.statusCode() + " - " + response.body();
        }
    }

    private String readText(JsonNode root) {
        JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
        if (!parts.isArray()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (JsonNode part : parts) {
            String text = part.path("text").asText("");
            if (StringUtils.hasText(text)) {
                if (!builder.isEmpty()) {
                    builder.append('\n');
                }
                builder.append(text);
            }
        }
        return builder.toString().trim();
    }

    private String stripMarkdownFence(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```[a-zA-Z]*\\s*", "");
            trimmed = trimmed.replaceFirst("\\s*```$", "");
        }
        return trimmed.trim();
    }
}
