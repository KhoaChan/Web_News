package com.example.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Thêm import này
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.entity.Article;
import com.example.news.service.ArticleService;
import com.example.news.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/article/{slug}")
    public String articleDetail(@PathVariable("slug") String slug, Model model) {
        var article = articleService.findBySlug(slug);
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.findAll());
        return "detail"; 
    }

    @GetMapping("/category/{slug}")
    public String categoryPage(@PathVariable String slug, 
                               @RequestParam(defaultValue = "0") int page, 
                               Model model) {
        int pageSize = 5;
        var categories = categoryService.findAll();
        var articlePage = articleService.findByCategory(slug, page, pageSize);
        
        model.addAttribute("categories", categories);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("currentCategorySlug", slug); 
        
        return "index"; 
    }

    @PostMapping("/article/comment")
    public String postComment(@RequestParam Long articleId, 
                          @RequestParam String name, 
                          @RequestParam String content) {
        articleService.saveComment(articleId, name, content);
        Article article = articleService.findById(articleId);
    return "redirect:/article/" + article.getSlug();
}
}