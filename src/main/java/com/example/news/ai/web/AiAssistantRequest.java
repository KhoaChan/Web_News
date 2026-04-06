package com.example.news.ai.web;

import java.util.List;

public record AiAssistantRequest(
        String currentPath,
        String pageTitle,
        String articleSlug,
        List<AiAssistantMessage> messages) {
}
