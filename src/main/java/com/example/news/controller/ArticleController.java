package com.example.news.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.entity.Article;
import com.example.news.entity.Comment;
import com.example.news.repository.ArticleRepository;
import com.example.news.repository.CommentRepository;
import com.example.news.service.ArticleService;
import com.example.news.service.CategoryService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @GetMapping("/article/{slug}")
    public String articleDetail(@PathVariable("slug") String slug, Model model, HttpSession session) {
        Article article = articleService.findBySlug(slug);
        
        // CHỐNG SPAM VIEW
        String sessionKey = "viewed_article_" + article.getId();
        if (session.getAttribute(sessionKey) == null) {
            article.setViews(article.getViews() + 1);
            articleService.save(article);
            session.setAttribute(sessionKey, true);
        }

        // BÀI VIẾT LIÊN QUAN: Phải đảm bảo chỉ lấy bài đã xuất bản (PUBLISHED)
        List<Article> relatedArticles = articleRepository
            .findTop3ByCategoryIdAndStatusAndIdNotOrderByCreatedAtDesc(
                article.getCategory().getId(), 
                "PUBLISHED", 
                article.getId()
            );

        // LỌC BÌNH LUẬN ĐÃ DUYỆT
        List<Comment> approvedComments = commentRepository
            .findByArticleIdAndStatusOrderByCreatedAtDesc(article.getId(), "APPROVED");

        model.addAttribute("article", article);
        model.addAttribute("relatedArticles", relatedArticles);
        model.addAttribute("comments", approvedComments);
        model.addAttribute("categories", categoryService.findAll());
        
        return "detail";
    }

    @GetMapping("/category/{slug}")
    public String categoryPage(@PathVariable String slug, 
                               @RequestParam(defaultValue = "0") int page, 
                               Model model) {
        int pageSize = 5;
        var categories = categoryService.findAll();
        
        var articlePage = articleService.findByCategoryAndStatus(slug, "PUBLISHED", page, pageSize);
        
        model.addAttribute("categories", categories);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("currentCategorySlug", slug); 
        
        return "index"; 
    }
}