package com.example.news.ai.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.news.ai.service.AiAssistantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAssistantService aiAssistantService;

    @PostMapping("/chat")
    public AiAssistantResponse chat(@RequestBody AiAssistantRequest request) {
        return aiAssistantService.chat(request);
    }
}
