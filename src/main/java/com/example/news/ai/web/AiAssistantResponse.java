package com.example.news.ai.web;

import java.util.List;

public record AiAssistantResponse(
        String reply,
        String readAloudText,
        List<String> suggestions,
        List<AiAssistantArticleCard> articles,
        boolean configured,
        String status) {
}
