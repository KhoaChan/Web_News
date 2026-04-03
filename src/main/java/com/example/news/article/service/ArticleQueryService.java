package com.example.news.article.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final ArticleRepository articleRepository;

    public Page<Article> findPublishedArticles(int page, int size) {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED, buildPageable(page, size));
    }

    public Page<Article> searchPublishedArticles(String keyword, int page, int size) {
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : "";
        return articleRepository.findByTitleContainingIgnoreCaseAndStatus(
                normalizedKeyword,
                ArticleStatus.PUBLISHED,
                buildPageable(page, size));
    }

    public Page<Article> findPublishedArticlesByCategory(String slug, int page, int size) {
        return articleRepository.findByCategorySlugAndStatus(slug, ArticleStatus.PUBLISHED, buildPageable(page, size));
    }

    @Transactional
    public Article getPublishedArticleDetail(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with slug: " + slug));
        article.setViews(article.getViews() + 1);
        return article;
    }

    public Article getPublishedArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Article is not available for the public site");
        }
        return article;
    }

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }
}
