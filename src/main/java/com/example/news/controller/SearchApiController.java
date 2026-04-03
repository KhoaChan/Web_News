package com.example.news.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.news.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchApiController {
    
    private final ArticleRepository articleRepository;

    @GetMapping("/search")
    public List<Map<String, String>> searchAjax(@RequestParam String q) {
        return articleRepository.findTop5ByTitleContainingIgnoreCaseAndStatus(q, "PUBLISHED")
                .stream().map(a -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("title", a.getTitle());
                    map.put("slug", a.getSlug());
                    map.put("thumb", a.getThumbnailUrl());
                    return map;
                }).collect(Collectors.toList());
    }
}