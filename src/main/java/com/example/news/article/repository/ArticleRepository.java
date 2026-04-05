package com.example.news.article.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

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

    @Query(value = """
            select a from Article a
            where a.status = :status
            order by coalesce(a.publishedAt, a.createdAt) desc
            """,
            countQuery = """
            select count(a) from Article a
            where a.status = :status
            """)
    Page<Article> findPublishedOrderedByFreshness(
            @Param("status") ArticleStatus status,
            Pageable pageable);

    @Query(value = """
            select a from Article a
            where a.status = :status
              and a.category.slug = :slug
            order by coalesce(a.publishedAt, a.createdAt) desc
            """,
            countQuery = """
            select count(a) from Article a
            where a.status = :status
              and a.category.slug = :slug
            """)
    Page<Article> findPublishedByCategoryOrderedByFreshness(
            @Param("slug") String slug,
            @Param("status") ArticleStatus status,
            Pageable pageable);

    @Query("""
            select a from Article a
            where a.status = :status
            order by a.views desc, coalesce(a.publishedAt, a.createdAt) desc
            """)
    List<Article> findMostViewedPublishedArticles(
            @Param("status") ArticleStatus status,
            Pageable pageable);

    @Query(value = """
            select a from Article a
            where a.status = :status
              and (:categorySlug is null or a.category.slug = :categorySlug)
              and (:fromTime is null or coalesce(a.publishedAt, a.createdAt) >= :fromTime)
            order by coalesce(a.publishedAt, a.createdAt) desc
            """,
            countQuery = """
            select count(a) from Article a
            where a.status = :status
              and (:categorySlug is null or a.category.slug = :categorySlug)
              and (:fromTime is null or coalesce(a.publishedAt, a.createdAt) >= :fromTime)
            """)
    Page<Article> findPublishedBySearchFiltersOrderedByFreshness(
            @Param("status") ArticleStatus status,
            @Param("categorySlug") String categorySlug,
            @Param("fromTime") LocalDateTime fromTime,
            Pageable pageable);

    @Query("""
            select a from Article a
            where a.status = :status
              and (:categorySlug is null or a.category.slug = :categorySlug)
              and (:fromTime is null or coalesce(a.publishedAt, a.createdAt) >= :fromTime)
            order by coalesce(a.publishedAt, a.createdAt) desc
            """)
    List<Article> findPublishedCandidatesForSearch(
            @Param("status") ArticleStatus status,
            @Param("categorySlug") String categorySlug,
            @Param("fromTime") LocalDateTime fromTime);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByCategoryId(Long categoryId);
}
