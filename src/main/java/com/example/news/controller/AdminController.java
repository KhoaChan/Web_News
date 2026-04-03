package com.example.news.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.entity.Article;
import com.example.news.entity.User;
import com.example.news.repository.UserRepository;
import com.example.news.service.ArticleService;
import com.example.news.service.CategoryService;
import com.example.news.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute("articles", articleService.findAll(0, 100).getContent());
        return "admin/dashboard";
    }

    @GetMapping("/article/create")
    public String createForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/article-form";
    }

    @PostMapping("/article/save")
public String saveArticle(@ModelAttribute("article") Article formArticle, 
                          @RequestParam("file") MultipartFile file, 
                          Principal principal) throws IOException {
    
    Article articleToSave;

    if (formArticle.getId() != null) {
        articleToSave = articleService.findById(formArticle.getId());
        
        // Cập nhật các nội dung mới từ form
        articleToSave.setTitle(formArticle.getTitle());
        articleToSave.setSlug(formArticle.getSlug());
        articleToSave.setSummary(formArticle.getSummary());
        articleToSave.setContent(formArticle.getContent());
        articleToSave.setCategory(formArticle.getCategory());
    } else {
        articleToSave = formArticle;
        String username = principal.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        articleToSave.setAuthor(author);
        articleToSave.setStatus("PUBLISHED");
    }

    if (!file.isEmpty()) {
        String imageUrl = fileUploadService.uploadFile(file);
        articleToSave.setThumbnailUrl(imageUrl);
    }

    articleService.save(articleToSave);
    
    return "redirect:/admin"; 
}

    @GetMapping("/article/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/article-form";
    }

    @GetMapping("/article/delete/{id}")
    public String deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteById(id);
        return "redirect:/admin";
    }

    // --- QUẢN LÝ CHUYÊN MỤC ---

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category-list";
    }

    @GetMapping("/category/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new com.example.news.entity.Category());
        return "admin/category-form";
    }

    @GetMapping("/category/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model) {
        var category = categoryService.findAll().stream()
                .filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("Không thấy chuyên mục"));
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute("category") com.example.news.entity.Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }
}