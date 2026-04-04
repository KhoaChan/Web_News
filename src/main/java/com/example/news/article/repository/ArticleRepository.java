package com.example.news.article.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    Optional<Article> findByIdAndAuthorUsername(Long id, String username);

    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    Page<Article> findByTitleContainingIgnoreCaseAndStatus(String title, ArticleStatus status, Pageable pageable);

    Page<Article> findByCategorySlugAndStatus(String slug, ArticleStatus status, Pageable pageable);

    List<Article> findByAuthorUsernameOrderByCreatedAtDesc(String username);

    List<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status);

    List<Article> findByStatusInOrderByCreatedAtDesc(Collection<ArticleStatus> statuses);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByCategoryId(Long categoryId);
}
