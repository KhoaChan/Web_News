package com.example.news.ai.web;

import java.util.List;

public record AiSearchResponse(
        String question,
        String normalizedQuestion,
        List<String> extractedKeywords,
        String answer,
        List<AiSearchResultItem> results) {
}
