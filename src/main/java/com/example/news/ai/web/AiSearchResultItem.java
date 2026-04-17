package com.example.news.ai.web;

public record AiSearchResultItem(
        Long id,
        String slug,
        String title,
        String summary,
        String category,
        int score) {
}
