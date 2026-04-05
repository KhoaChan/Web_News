package com.example.news.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.article.entity.ArticleStatus;
import com.example.news.user.entity.ViewedArticle;

public interface ViewedArticleRepository extends JpaRepository<ViewedArticle, Long> {

    Optional<ViewedArticle> findByUserIdAndArticleId(Long userId, Long articleId);

    long countByUserId(Long userId);

    List<ViewedArticle> findByUserIdAndArticleStatusOrderByViewedAtDesc(Long userId, ArticleStatus status);
}
