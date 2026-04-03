package com.example.news.article.controller;

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
        populateListModel(model, articlePage, page);
        return "index";
    }

    @GetMapping("/search")
    public String search(
            Model model,
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page) {
        Page<Article> articlePage = articleQueryService.searchPublishedArticles(keyword, page, PAGE_SIZE);
        populateListModel(model, articlePage, page);
        if (StringUtils.hasText(keyword)) {
            model.addAttribute("keyword", keyword.trim());
        }
        return "index";
    }

    private void populateListModel(Model model, Page<Article> articlePage, int page) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
    }
}
