package com.example.news.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,
        String model,
        String baseUrl) {

    public String resolvedModel() {
        return model == null || model.isBlank() ? "gemini-2.5-flash" : model;
    }

    public String resolvedBaseUrl() {
        return baseUrl == null || baseUrl.isBlank()
                ? "https://generativelanguage.googleapis.com/v1beta"
                : baseUrl;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
