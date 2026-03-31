package com.example.news.service;

import org.springframework.data.domain.Page;

import com.example.news.entity.Article;

public interface ArticleService {

    Page<Article> findAll(int page, int size);

    Page<Article> search(String keyword, int page, int size);
    Page<Article> findByCategory(String slug, int page, int size);
    
    Article findBySlug(String slug);

    Article save(Article article);

    Article findById(Long id);

    void deleteById(Long id);

    void saveComment(Long articleId, String name, String content);
}