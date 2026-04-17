package com.example.news.ai.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.news.ai.web.AiAssistantArticleCard;
import com.example.news.ai.web.AiAssistantMessage;
import com.example.news.ai.web.AiAssistantRequest;
import com.example.news.ai.web.AiAssistantResponse;
import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantService {

    private static final int RECENT_ARTICLE_LIMIT = 8;
    private static final int RELATED_ARTICLE_LIMIT = 4;

    private final ArticleQueryService articleQueryService;
    private final GeminiResponsesClient geminiResponsesClient;
    private final ObjectMapper objectMapper;

    public AiAssistantResponse chat(AiAssistantRequest request) {
        if (!geminiResponsesClient.isConfigured()) {
            return new AiAssistantResponse(
                    "Gemini chua duoc cau hinh. Hay them GEMINI_API_KEY de bat tro ly AI.",
                    "",
                    List.of("Tom tat bai nay", "Tim bai viet ve AI", "Goi y bai dang doc"),
                    List.of(),
                    false,
                    "not_configured");
        }

        try {
            Article currentArticle = StringUtils.hasText(request.articleSlug())
                    ? articleQueryService.getPublishedArticleBySlug(request.articleSlug())
                    : null;

            List<Article> candidateArticles = buildCandidateArticles(currentArticle);
            List<Map<String, Object>> input = buildModelInput(request, currentArticle, candidateArticles);
            String json = geminiResponsesClient.createStructuredResponse(input, buildResponseSchema());
            JsonNode root = objectMapper.readTree(json);

            return new AiAssistantResponse(
                    root.path("reply").asText(""),
                    root.path("readAloudText").asText(""),
                    readSuggestions(root.path("suggestions")),
                    readArticleCards(root.path("articles"), candidateArticles),
                    true,
                    "ok");
        } catch (Exception exception) {
            log.error("AI assistant request failed", exception);
            return new AiAssistantResponse(
                    buildAiErrorMessage(exception),
                    "",
                    List.of("Tom tat bai nay", "Tim bai cung chu de", "Goi y doc nhanh"),
                    List.of(),
                    geminiResponsesClient.isConfigured(),
                    "error");
        }
    }

    private List<Map<String, Object>> buildModelInput(
            AiAssistantRequest request,
            Article currentArticle,
            List<Article> candidateArticles) {
        List<Map<String, Object>> input = new ArrayList<>();
        input.add(message("developer", """
                Ban la tro ly AI chuyen nghiep cho website tin tuc News.
                Nhiem vu:
                - Tra loi bang tieng Viet, van phong gon, ro, lich su.
                - Khong mo dau cau tra loi bang cac nhan de nhu: "Tom tat:", "Goi y:", "Noi dung:", "Ket luan:".
                - Tra loi truc tiep vao noi dung, khong tao tieu de phu neu nguoi dung khong yeu cau.
                - Chi duoc dua tren ngu canh website duoc cung cap.
                - Neu nguoi dung muon tim bai viet, chi de xuat trong danh sach ung vien da cho.
                - Neu nguoi dung muon tom tat bai hien tai, uu tien current_article.
                - Neu khong du thong tin, noi ro rang la chua co du lieu thay vi doan.
                - Luon tra ve JSON hop le.
                - JSON phai co cac khoa: reply, readAloudText, suggestions, articles.
                - articles la danh sach bai viet co slug nam trong candidate_articles.
                """));
        input.add(message("user", objectToJson(buildContextPayload(request, currentArticle, candidateArticles))));

        List<AiAssistantMessage> messages = request.messages() == null ? List.of() : request.messages();
        int start = Math.max(0, messages.size() - 8);
        for (AiAssistantMessage message : messages.subList(start, messages.size())) {
            if (!StringUtils.hasText(message.content())) {
                continue;
            }
            String role = "assistant".equalsIgnoreCase(message.role()) ? "assistant" : "user";
            input.add(message(role, message.content()));
        }
        return input;
    }

    private Map<String, Object> buildContextPayload(
            AiAssistantRequest request,
            Article currentArticle,
            List<Article> candidateArticles) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("page", Map.of(
                "currentPath", Objects.toString(request.currentPath(), ""),
                "pageTitle", Objects.toString(request.pageTitle(), "")));
        payload.put("current_article", currentArticle == null ? null : Map.of(
                "slug", currentArticle.getSlug(),
                "title", currentArticle.getTitle(),
                "summary", Objects.toString(currentArticle.getSummary(), ""),
                "content", stripHtml(currentArticle.getContent()),
                "category", currentArticle.getCategory() != null ? currentArticle.getCategory().getName() : ""));
        payload.put("candidate_articles", candidateArticles.stream()
                .map(article -> Map.of(
                        "slug", article.getSlug(),
                        "title", article.getTitle(),
                        "summary", Objects.toString(article.getSummary(), ""),
                        "category", article.getCategory() != null ? article.getCategory().getName() : "",
                        "views", article.getViews()))
                .toList());
        return payload;
    }

    private List<Article> buildCandidateArticles(Article currentArticle) {
        Set<Long> seenIds = new LinkedHashSet<>();
        List<Article> candidates = new ArrayList<>();

        Page<Article> recentPage = articleQueryService.findPublishedArticles(0, RECENT_ARTICLE_LIMIT);
        addArticles(candidates, seenIds, recentPage.getContent());
        addArticles(candidates, seenIds, articleQueryService.findMostViewedPublishedArticles());

        if (currentArticle != null) {
            addArticles(candidates, seenIds,
                    articleQueryService.findRelatedPublishedArticles(currentArticle.getId(), extractKeywords(currentArticle), RELATED_ARTICLE_LIMIT));
            candidates.removeIf(article -> article.getId().equals(currentArticle.getId()));
        }
        return candidates.stream().limit(16).toList();
    }

    private void addArticles(List<Article> target, Set<Long> seenIds, List<Article> articles) {
        for (Article article : articles) {
            if (article == null || article.getId() == null || !seenIds.add(article.getId())) {
                continue;
            }
            target.add(article);
        }
    }

    private List<String> extractKeywords(Article article) {
        String text = (Objects.toString(article.getTitle(), "") + " "
                + Objects.toString(article.getSummary(), "") + " "
                + stripHtml(article.getContent())).toLowerCase();
        return Arrays.stream(text.split("[^\\p{L}\\p{N}]+"))
                .filter(StringUtils::hasText)
                .filter(token -> token.length() >= 4)
                .distinct()
                .limit(8)
                .toList();
    }

    private Map<String, Object> buildResponseSchema() {
        Map<String, Object> articleSchema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "slug", Map.of("type", "string"),
                        "title", Map.of("type", "string"),
                        "summary", Map.of("type", "string"),
                        "reason", Map.of("type", "string")),
                "required", List.of("slug", "title", "summary", "reason"));

        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "reply", Map.of("type", "string"),
                        "readAloudText", Map.of("type", "string"),
                        "suggestions", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")),
                        "articles", Map.of(
                                "type", "array",
                                "items", articleSchema)),
                "required", List.of("reply", "readAloudText", "suggestions", "articles"));
    }

    private List<String> readSuggestions(JsonNode node) {
        List<String> suggestions = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> suggestions.add(item.asText("")));
        }
        return suggestions.stream()
                .filter(StringUtils::hasText)
                .limit(4)
                .toList();
    }

    private List<AiAssistantArticleCard> readArticleCards(JsonNode node, List<Article> candidates) {
        Map<String, Article> articleBySlug = candidates.stream()
                .collect(Collectors.toMap(Article::getSlug, article -> article, (left, right) -> left));

        List<AiAssistantArticleCard> cards = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                String slug = item.path("slug").asText("");
                Article article = articleBySlug.get(slug);
                if (article == null) {
                    continue;
                }
                cards.add(new AiAssistantArticleCard(
                        article.getSlug(),
                        article.getTitle(),
                        StringUtils.hasText(item.path("summary").asText()) ? item.path("summary").asText() : Objects.toString(article.getSummary(), ""),
                        item.path("reason").asText("")));
            }
        }
        return cards.stream().limit(4).toList();
    }

    private Map<String, Object> message(String role, String text) {
        return Map.of(
                "role", role,
                "content", text);
    }

    private String objectToJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot serialize AI context", exception);
        }
    }

    private String stripHtml(String value) {
        return Objects.toString(value, "").replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
    }

    private String buildAiErrorMessage(Exception exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("API_KEY")) {
            return "Gemini API key khong hop le. Hay kiem tra lai GEMINI_API_KEY.";
        }
        if (message != null && (message.contains("PERMISSION_DENIED") || message.contains("403"))) {
            return "Gemini tu choi yeu cau. Hay kiem tra API key va quyen truy cap Gemini API trong Google AI Studio.";
        }
        if (message != null && (message.contains("RESOURCE_EXHAUSTED") || message.contains("quota"))) {
            return "Gemini dang het quota hoac bi gioi han tam thoi. Hay kiem tra quota trong Google AI Studio.";
        }
        if (message != null && (message.contains("models/") || message.contains("not found"))) {
            return "Model Gemini khong ton tai hoac khong kha dung voi API key hien tai. App da tu thu model fallback, nen neu van loi hay kiem tra lai GEMINI_MODEL va dam bao ban dang dung API key tu Google AI Studio.";
        }
        if (message != null && message.contains("429")) {
            return "Gemini dang gioi han yeu cau tam thoi. Vui long thu lai sau it phut.";
        }
        return "Tro ly AI tam thoi gap loi khi ket noi Gemini. Hay kiem tra GEMINI_API_KEY, GEMINI_MODEL, hoac xem log server de biet loi chi tiet.";
    }
}
