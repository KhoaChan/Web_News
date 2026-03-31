package com.example.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.service.ArticleService;
import com.example.news.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 5;
        var articlePage = articleService.findAll(page, pageSize);
        
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        return "index"; 
    }

    @GetMapping("/search")
    public String search(Model model, 
                         @RequestParam("keyword") String keyword,
                         @RequestParam(defaultValue = "0") int page) {
        int pageSize = 5;
        var articlePage = articleService.search(keyword, page, pageSize);
        
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "index";
    }
}