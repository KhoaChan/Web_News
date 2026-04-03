package com.example.news.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.news.article.repository.ArticleRepository;
import com.example.news.category.entity.Category;
import com.example.news.category.repository.CategoryRepository;
import com.example.news.category.web.CategoryForm;
import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    public CategoryForm buildForm() {
        return new CategoryForm();
    }

    public CategoryForm getCategoryForm(Long id) {
        Category category = getById(id);
        CategoryForm form = new CategoryForm();
        form.setId(category.getId());
        form.setName(category.getName());
        form.setSlug(category.getSlug());
        form.setDescription(category.getDescription());
        return form;
    }

    public void validateCategoryForm(CategoryForm form, BindingResult bindingResult) {
        if (form.getId() == null) {
            if (categoryRepository.existsBySlug(form.getSlug())) {
                bindingResult.rejectValue("slug", "duplicate", "Slug already exists");
            }
            if (categoryRepository.existsByNameIgnoreCase(form.getName())) {
                bindingResult.rejectValue("name", "duplicate", "Category name already exists");
            }
            return;
        }

        if (categoryRepository.existsBySlugAndIdNot(form.getSlug(), form.getId())) {
            bindingResult.rejectValue("slug", "duplicate", "Slug already exists");
        }
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(form.getName(), form.getId())) {
            bindingResult.rejectValue("name", "duplicate", "Category name already exists");
        }
    }

    public Category createCategory(CategoryForm form) {
        Category category = new Category();
        mapFormToCategory(form, category);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryForm form) {
        Category category = getById(id);
        mapFormToCategory(form, category);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getById(id);
        if (articleRepository.existsByCategoryId(category.getId())) {
            throw new InvalidOperationException("Cannot delete a category that still has articles");
        }
        categoryRepository.delete(category);
    }

    private void mapFormToCategory(CategoryForm form, Category category) {
        category.setName(form.getName().trim());
        category.setSlug(form.getSlug().trim());
        category.setDescription(form.getDescription() == null ? null : form.getDescription().trim());
    }
}
