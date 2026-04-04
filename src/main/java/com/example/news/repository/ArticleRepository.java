package com.example.news.repository;

import java.util.List;
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

    List<Article> findTop3ByCategoryIdAndIdNotOrderByCreatedAtDesc(Long categoryId, String status ,Long articleId);

    List<Article> findTop5ByTitleContainingIgnoreCaseAndStatus(String title, String status);
    
    List<Article> findByStatusOrderByIdDesc(String status);

    List<Article> findTop3ByCategoryIdAndStatusAndIdNotOrderByCreatedAtDesc(Long categoryId, String status, Long articleId);

    // Dành cho Trang chủ (Có phân trang)
    Page<Article> findByStatus(String status, Pageable pageable);

    
}