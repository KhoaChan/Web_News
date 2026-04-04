package com.example.news.article.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.article.service.ArticleManagementService;
import com.example.news.article.service.ArticleWorkflowService;
import com.example.news.article.web.ArticleForm;
import com.example.news.category.service.CategoryService;
import com.example.news.user.security.NewsUserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthorArticleController {

    private final ArticleWorkflowService articleWorkflowService;
    private final ArticleManagementService articleManagementService;
    private final CategoryService categoryService;

    @GetMapping("/author")
    public String dashboard(@AuthenticationPrincipal NewsUserPrincipal principal, Model model) {
        model.addAttribute("articles", articleWorkflowService.findAuthorArticles(principal.getUsername()));
        populateLayoutModel(
                model,
                "My Articles",
                "Create drafts, submit them for review, and track editorial feedback.",
                "author_articles");
        return "author/dashboard";
    }

    @GetMapping("/author/article/create")
    public String createForm(Model model) {
        populateFormModel(model, articleWorkflowService.buildAuthorForm(), true);
        return "author/article-form";
    }

    @GetMapping("/author/article/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            @AuthenticationPrincipal NewsUserPrincipal principal,
            Model model) {
        populateFormModel(model, articleWorkflowService.getAuthorArticleForm(id, principal.getUsername()), false);
        return "author/article-form";
    }

    @PostMapping("/author/article/save")
    public String saveArticle(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("articleForm") ArticleForm articleForm,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes) {
        articleManagementService.validateArticleForm(articleForm, bindingResult);
        if (bindingResult.hasErrors()) {
            populateFormModel(model, articleForm, articleForm.getId() == null);
            return "author/article-form";
        }

        if (articleForm.getId() == null) {
            articleWorkflowService.createAuthorArticle(articleForm, file, principal.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Draft saved successfully.");
        } else {
            articleWorkflowService.updateAuthorArticle(articleForm.getId(), articleForm, file, principal.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Draft updated successfully.");
        }
        return "redirect:/author";
    }

    @PostMapping("/author/article/submit/{id}")
    public String submitForReview(
            @PathVariable Long id,
            @AuthenticationPrincipal NewsUserPrincipal principal,
            RedirectAttributes redirectAttributes) {
        articleWorkflowService.submitForReview(id, principal.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Article submitted for editorial review.");
        return "redirect:/author";
    }

    @PostMapping("/author/article/cancel/{id}")
    public String cancelArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal NewsUserPrincipal principal,
            RedirectAttributes redirectAttributes) {
        articleWorkflowService.cancelByAuthor(id, principal.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Article cancelled successfully.");
        return "redirect:/author";
    }

    private void populateFormModel(Model model, ArticleForm articleForm, boolean isCreate) {
        model.addAttribute("articleForm", articleForm);
        model.addAttribute("categories", categoryService.findAll());
        populateLayoutModel(
                model,
                isCreate ? "Create Draft" : "Edit Draft",
                isCreate
                        ? "Save content privately, then submit it when it is ready for editorial review."
                        : "Update your draft, respond to review notes, and keep it ready for resubmission.",
                isCreate ? "author_create" : "author_articles");
    }

    private void populateLayoutModel(Model model, String pageTitle, String pageSubtitle, String activeKey) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageSubtitle", pageSubtitle);
        model.addAttribute("activeKey", activeKey);
    }
}
