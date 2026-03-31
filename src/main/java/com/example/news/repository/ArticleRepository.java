package com.example.news.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.news.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Optional<Article> findBySlug(String slug);

    Page<Article> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

    Page<Article> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    Page<Article> findByTitleContainingIgnoreCaseAndStatus(String title, String status, Pageable pageable);

    Page<Article> findByCategorySlugAndStatus(String slug, String status, Pageable pageable);
    
}