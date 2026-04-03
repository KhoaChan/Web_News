package com.example.news.article.controller;

import java.security.Principal;

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

import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.service.ArticleManagementService;
import com.example.news.article.web.ArticleForm;
import com.example.news.category.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleManagementService articleManagementService;
    private final CategoryService categoryService;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("articles", articleManagementService.findAllForAdmin());
        return "admin/dashboard";
    }

    @GetMapping("/admin/article/create")
    public String createForm(Model model) {
        populateFormModel(model, articleManagementService.buildForm());
        return "admin/article-form";
    }

    @GetMapping("/admin/article/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        populateFormModel(model, articleManagementService.getArticleForm(id));
        return "admin/article-form";
    }

    @PostMapping("/admin/article/save")
    public String saveArticle(
            @Valid @ModelAttribute("articleForm") ArticleForm articleForm,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        articleManagementService.validateArticleForm(articleForm, bindingResult);
        if (bindingResult.hasErrors()) {
            populateFormModel(model, articleForm);
            return "admin/article-form";
        }

        if (articleForm.getId() == null) {
            articleManagementService.createArticle(articleForm, file, principal == null ? null : principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Article created successfully.");
        } else {
            articleManagementService.updateArticle(articleForm.getId(), articleForm, file);
            redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully.");
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/article/delete/{id}")
    public String deleteArticle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleManagementService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully.");
        return "redirect:/admin";
    }

    private void populateFormModel(Model model, ArticleForm articleForm) {
        model.addAttribute("articleForm", articleForm);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("articleStatuses", ArticleStatus.values());
    }
}
