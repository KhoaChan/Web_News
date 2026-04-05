package com.example.news.article.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;
import com.example.news.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final int PAGE_SIZE = 5;

    private final CategoryService categoryService;
    private final ArticleQueryService articleQueryService;

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Article> articlePage = articleQueryService.findPublishedArticles(page, PAGE_SIZE);
        populateIndexModel(model, articlePage, page);
        return "index";
    }

    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "all") String time,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page) {
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : "";
        String normalizedTime = normalizeTime(time);
        String normalizedCategory = normalizeCategory(category);
        String normalizedSort = normalizeSort(sort, normalizedKeyword);

        Page<Article> articlePage = articleQueryService.searchPublishedArticles(
                normalizedKeyword,
                normalizedCategory,
                normalizedTime,
                normalizedSort,
                page,
                PAGE_SIZE);

        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("availableCategories", categoryService.findAll());
        model.addAttribute("mostViewedArticles", articleQueryService.findMostViewedPublishedArticles());
        model.addAttribute("keyword", normalizedKeyword);
        model.addAttribute("selectedTime", normalizedTime);
        model.addAttribute("selectedCategory", normalizedCategory);
        model.addAttribute("selectedSort", normalizedSort);
        model.addAttribute("timeOptions", buildTimeOptions());
        return "search";
    }

    private void populateIndexModel(Model model, Page<Article> articlePage, int page) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("mostViewedArticles", articleQueryService.findMostViewedPublishedArticles());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
    }

    private Map<String, String> buildTimeOptions() {
        Map<String, String> timeOptions = new LinkedHashMap<>();
        timeOptions.put("all", "Tất cả");
        timeOptions.put("24h", "24 giờ qua");
        timeOptions.put("7d", "7 ngày qua");
        timeOptions.put("30d", "30 ngày qua");
        timeOptions.put("1y", "1 năm qua");
        return timeOptions;
    }

    private String normalizeTime(String time) {
        if (!StringUtils.hasText(time)) {
            return "all";
        }
        return switch (time.trim().toLowerCase()) {
            case "24h", "7d", "30d", "1y" -> time.trim().toLowerCase();
            default -> "all";
        };
    }

    private String normalizeCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return "all";
        }
        return category.trim();
    }

    private String normalizeSort(String sort, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "newest";
        }
        return "relevant".equalsIgnoreCase(sort) ? "relevant" : "newest";
    }
}
