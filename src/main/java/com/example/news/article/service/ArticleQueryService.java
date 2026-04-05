package com.example.news.article.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleQueryService {

    private static final int MOST_VIEWED_LIMIT = 6;

    private final ArticleRepository articleRepository;

    public Page<Article> findPublishedArticles(int page, int size) {
        return articleRepository.findPublishedOrderedByFreshness(ArticleStatus.PUBLISHED, PageRequest.of(page, size));
    }

    public Page<Article> searchPublishedArticles(
            String keyword,
            String categorySlug,
            String timeFilter,
            String sort,
            int page,
            int size) {
        String normalizedSort = normalizeSort(sort);
        if ("relevant".equals(normalizedSort) && !StringUtils.hasText(keyword)) {
            normalizedSort = "newest";
        }

        if ("relevant".equals(normalizedSort)) {
            return searchPublishedArticlesByRelevance(keyword, categorySlug, timeFilter, page, size);
        }
        return searchPublishedArticlesByNewest(keyword, categorySlug, timeFilter, page, size);
    }

    public Page<Article> findPublishedArticlesByCategory(String slug, int page, int size) {
        return articleRepository.findPublishedByCategoryOrderedByFreshness(slug, ArticleStatus.PUBLISHED, PageRequest.of(page, size));
    }

    public List<Article> findMostViewedPublishedArticles() {
        return articleRepository.findMostViewedPublishedArticles(ArticleStatus.PUBLISHED, PageRequest.of(0, MOST_VIEWED_LIMIT));
    }

    @Transactional
    public Article getPublishedArticleDetail(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với đường dẫn: " + slug));
        article.setViews(article.getViews() + 1);
        return article;
    }

    public Article getPublishedArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với ID: " + id));
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Bài viết hiện chưa sẵn sàng để hiển thị công khai");
        }
        return article;
    }

    private Page<Article> searchPublishedArticlesByNewest(
            String keyword,
            String categorySlug,
            String timeFilter,
            int page,
            int size) {
        String normalizedKeyword = normalizeKeyword(keyword);
        String normalizedCategorySlug = normalizeCategorySlug(categorySlug);
        LocalDateTime fromTime = resolveFromTime(timeFilter);

        Page<Article> filteredPage = articleRepository.findPublishedBySearchFiltersOrderedByFreshness(
                ArticleStatus.PUBLISHED,
                normalizedCategorySlug,
                fromTime,
                PageRequest.of(page, size));

        if (!StringUtils.hasText(normalizedKeyword)) {
            return filteredPage;
        }

        List<Article> matchedArticles = articleRepository.findPublishedCandidatesForSearch(
                        ArticleStatus.PUBLISHED,
                        normalizedCategorySlug,
                        fromTime)
                .stream()
                .filter(article -> calculateRelevanceScore(article, normalizedKeyword) > 0)
                .sorted(Comparator.comparing(this::getFreshnessTime).reversed())
                .toList();

        return toPage(matchedArticles, page, size);
    }

    private Page<Article> searchPublishedArticlesByRelevance(
            String keyword,
            String categorySlug,
            String timeFilter,
            int page,
            int size) {
        String normalizedKeyword = normalizeKeyword(keyword);
        String normalizedCategorySlug = normalizeCategorySlug(categorySlug);
        LocalDateTime fromTime = resolveFromTime(timeFilter);

        List<Article> matchedArticles = articleRepository.findPublishedCandidatesForSearch(
                        ArticleStatus.PUBLISHED,
                        normalizedCategorySlug,
                        fromTime)
                .stream()
                .map(article -> new ScoredArticle(article, calculateRelevanceScore(article, normalizedKeyword)))
                .filter(scoredArticle -> scoredArticle.score() > 0)
                .sorted(Comparator
                        .comparingInt(ScoredArticle::score)
                        .reversed()
                        .thenComparing(scoredArticle -> getFreshnessTime(scoredArticle.article()), Comparator.reverseOrder()))
                .map(ScoredArticle::article)
                .toList();

        return toPage(matchedArticles, page, size);
    }

    private Page<Article> toPage(List<Article> articles, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), articles.size());
        int end = Math.min(start + pageable.getPageSize(), articles.size());
        return new PageImpl<>(articles.subList(start, end), pageable, articles.size());
    }

    private int calculateRelevanceScore(Article article, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0;
        }

        String title = normalizeText(article.getTitle());
        String summary = normalizeText(article.getSummary());
        String content = normalizeText(article.getContent());

        return Arrays.stream(keyword.split("\\s+"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .map(token -> token.toLowerCase(Locale.ROOT))
                .mapToInt(token -> {
                    int score = 0;
                    if (title.contains(token)) {
                        score += 3;
                    }
                    if (summary.contains(token)) {
                        score += 2;
                    }
                    if (content.contains(token)) {
                        score += 1;
                    }
                    return score;
                })
                .sum();
    }

    private String normalizeKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? keyword.trim().toLowerCase(Locale.ROOT) : "";
    }

    private String normalizeCategorySlug(String categorySlug) {
        if (!StringUtils.hasText(categorySlug) || "all".equalsIgnoreCase(categorySlug.trim())) {
            return null;
        }
        return categorySlug.trim();
    }

    private String normalizeSort(String sort) {
        return "relevant".equalsIgnoreCase(sort) ? "relevant" : "newest";
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.toLowerCase(Locale.ROOT) : "";
    }

    private LocalDateTime resolveFromTime(String timeFilter) {
        if (!StringUtils.hasText(timeFilter)) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        return switch (timeFilter.trim().toLowerCase(Locale.ROOT)) {
            case "24h" -> now.minusHours(24);
            case "7d" -> now.minusDays(7);
            case "30d" -> now.minusDays(30);
            case "1y" -> now.minusYears(1);
            default -> null;
        };
    }

    private LocalDateTime getFreshnessTime(Article article) {
        return Objects.requireNonNullElse(article.getPublishedAt(), article.getCreatedAt());
    }

    private record ScoredArticle(Article article, int score) {
    }
}
