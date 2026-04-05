package com.example.news.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.article.entity.ArticleStatus;
import com.example.news.user.entity.SavedArticle;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {

    Optional<SavedArticle> findByUserIdAndArticleId(Long userId, Long articleId);

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    long countByUserId(Long userId);

    List<SavedArticle> findByUserIdAndArticleStatusOrderBySavedAtDesc(Long userId, ArticleStatus status);
}
