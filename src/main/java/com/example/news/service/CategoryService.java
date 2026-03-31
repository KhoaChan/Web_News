package com.example.news.service;

import java.util.List;

import com.example.news.entity.Category;

public interface CategoryService {
    
    List<Category> findAll();

    Category findBySlug(String slug);

    Category save(Category category);

    void deleteById(Long id);
}