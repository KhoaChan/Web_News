package com.example.news.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.news.category.service.CategoryService;
import com.example.news.category.web.CategoryForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/admin/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        populateLayoutModel(
                model,
                "Chuyên mục",
                "Quản lý tên chuyên mục, slug và các quy tắc xóa an toàn.",
                "admin_categories");
        return "admin/category-list";
    }

    @GetMapping("/admin/category/create")
    public String createCategoryForm(Model model) {
        populateFormModel(model, categoryService.buildForm());
        return "admin/category-form";
    }

    @GetMapping("/admin/category/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model) {
        populateFormModel(model, categoryService.getCategoryForm(id));
        return "admin/category-form";
    }

    @PostMapping("/admin/category/save")
    public String saveCategory(
            @Valid @ModelAttribute("categoryForm") CategoryForm categoryForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        categoryService.validateCategoryForm(categoryForm, bindingResult);
        if (bindingResult.hasErrors()) {
            populateFormModel(model, categoryForm);
            return "admin/category-form";
        }

        if (categoryForm.getId() == null) {
            categoryService.createCategory(categoryForm);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo chuyên mục thành công.");
        } else {
            categoryService.updateCategory(categoryForm.getId(), categoryForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chuyên mục thành công.");
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyên mục thành công.");
        return "redirect:/admin/categories";
    }

    private void populateFormModel(Model model, CategoryForm categoryForm) {
        model.addAttribute("categoryForm", categoryForm);
        populateLayoutModel(
                model,
                categoryForm.getId() == null ? "Tạo chuyên mục" : "Chỉnh sửa chuyên mục",
                "Giữ dữ liệu chuyên mục duy nhất và dễ quản lý.",
                "admin_categories");
    }

    private void populateLayoutModel(Model model, String pageTitle, String pageSubtitle, String activeKey) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageSubtitle", pageSubtitle);
        model.addAttribute("activeKey", activeKey);
    }
}
