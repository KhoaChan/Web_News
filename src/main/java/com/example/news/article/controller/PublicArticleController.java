package com.example.news.article.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;
import com.example.news.category.service.CategoryService;
import com.example.news.comment.service.CommentService;
import com.example.news.comment.web.CommentForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PublicArticleController {

    private static final int PAGE_SIZE = 5;

    private final ArticleQueryService articleQueryService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/article/{slug}")
    public String articleDetail(
            @PathVariable("slug") String slug,
            Model model,
            @ModelAttribute("commentForm") CommentForm commentForm) {
        Article article = articleQueryService.getPublishedArticleDetail(slug);
        if (commentForm.getArticleId() == null) {
            commentForm.setArticleId(article.getId());
        }
        populateDetailModel(model, article);
        return "detail";
    }

    @GetMapping("/category/{slug}")
    public String categoryPage(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<Article> articlePage = articleQueryService.findPublishedArticlesByCategory(slug, page, PAGE_SIZE);

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("currentCategorySlug", slug);

        return "index";
    }

    @PostMapping("/article/comment")
    public String postComment(
            @Valid @ModelAttribute("commentForm") CommentForm commentForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Article article = articleQueryService.getPublishedArticleById(commentForm.getArticleId());
        if (bindingResult.hasErrors()) {
            populateDetailModel(model, article);
            return "detail";
        }

        commentService.createComment(commentForm);
        redirectAttributes.addFlashAttribute("successMessage", "Comment submitted successfully.");
        return "redirect:/article/" + article.getSlug();
    }

    private void populateDetailModel(Model model, Article article) {
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.findAll());
    }
}
