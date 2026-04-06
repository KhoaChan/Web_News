package com.example.news.ai.web;

import java.util.List;

public record AiArticleInsightsResponse(
        String articleSlug,
        String articleTitle,
        String cleanText,
        String shortSummary,
        String detailedSummary,
        List<String> keyPoints,
        List<String> keywords,
        List<String> suggestedQuestions,
        int readingTimeMinutes,
        int wordCount,
        String tone,
        String suggestedAudience,
        List<AiRelatedArticleItem> relatedArticles) {
}
