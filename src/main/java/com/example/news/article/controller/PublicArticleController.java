package com.example.news.article.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.news.user.security.NewsUserPrincipal;
import com.example.news.user.service.ReaderActivityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PublicArticleController {

    private static final int PAGE_SIZE = 5;

    private final ArticleQueryService articleQueryService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final ReaderActivityService readerActivityService;

    @GetMapping("/article/{slug}")
    public String articleDetail(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal NewsUserPrincipal principal,
            Model model,
            @ModelAttribute("commentForm") CommentForm commentForm) {
        Article article = articleQueryService.getPublishedArticleDetail(slug);
        if (principal != null) {
            readerActivityService.recordViewedArticle(principal.getId(), article.getId());
            if (commentForm.getName() == null || commentForm.getName().isBlank()) {
                commentForm.setName(principal.getDisplayName());
            }
        }
        if (commentForm.getArticleId() == null) {
            commentForm.setArticleId(article.getId());
        }
        populateDetailModel(model, article, principal);
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
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("commentForm") CommentForm commentForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Article article = articleQueryService.getPublishedArticleById(commentForm.getArticleId());
        if (principal != null && (commentForm.getName() == null || commentForm.getName().isBlank())) {
            commentForm.setName(principal.getDisplayName());
        }

        if (bindingResult.hasErrors()) {
            populateDetailModel(model, article, principal);
            return "detail";
        }

        commentService.createComment(commentForm, principal == null ? null : principal.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi bình luận và đang chờ duyệt.");
        return "redirect:/article/" + article.getSlug();
    }

    @PostMapping("/article/save/{id}")
    public String toggleSavedArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @RequestParam(value = "redirectTo", required = false) String redirectTo,
            RedirectAttributes redirectAttributes) {
        boolean saved = readerActivityService.toggleSavedArticle(principal.getId(), id);
        redirectAttributes.addFlashAttribute("successMessage", saved ? "Đã lưu bài viết." : "Đã bỏ lưu bài viết.");
        if (redirectTo != null && redirectTo.startsWith("/")) {
            return "redirect:" + redirectTo;
        }
        Article article = articleQueryService.getPublishedArticleById(id);
        return "redirect:/article/" + article.getSlug();
    }

    private void populateDetailModel(Model model, Article article, NewsUserPrincipal principal) {
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("approvedComments", commentService.findApprovedCommentsForArticle(article.getId()));
        model.addAttribute("approvedCommentCount", commentService.countApprovedCommentsForArticle(article.getId()));
        model.addAttribute("savedArticle", principal != null && readerActivityService.isArticleSaved(principal.getId(), article.getId()));
    }
}
