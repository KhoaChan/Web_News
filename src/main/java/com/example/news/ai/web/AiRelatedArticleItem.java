package com.example.news.ai.web;

public record AiRelatedArticleItem(
        Long id,
        String slug,
        String title,
        String summary,
        String category) {
}
