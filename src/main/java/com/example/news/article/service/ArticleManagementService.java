package com.example.news.article.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.article.web.ArticleForm;
import com.example.news.category.service.CategoryService;
import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.common.storage.StorageService;
import com.example.news.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleManagementService {

    private final ArticleRepository articleRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public List<Article> findAllForAdmin() {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public ArticleForm buildForm() {
        ArticleForm form = new ArticleForm();
        form.setStatus(ArticleStatus.DRAFT.name());
        return form;
    }

    public ArticleForm getArticleForm(Long id) {
        Article article = getArticle(id);
        ArticleForm form = new ArticleForm();
        form.setId(article.getId());
        form.setTitle(article.getTitle());
        form.setSlug(article.getSlug());
        form.setSummary(article.getSummary());
        form.setContent(article.getContent());
        form.setCategoryId(article.getCategory().getId());
        form.setStatus(article.getStatus().name());
        form.setReviewNote(article.getReviewNote());
        form.setThumbnailUrl(article.getThumbnailUrl());
        return form;
    }

    public Article getArticleById(Long id) {
        return getArticle(id);
    }

    public void validateArticleForm(ArticleForm form, BindingResult bindingResult) {
        if (form.getCategoryId() != null) {
            try {
                categoryService.getById(form.getCategoryId());
            } catch (ResourceNotFoundException exception) {
                bindingResult.rejectValue("categoryId", "notFound", "Category not found");
            }
        }

        if (form.getId() == null) {
            if (articleRepository.existsBySlug(form.getSlug())) {
                bindingResult.rejectValue("slug", "duplicate", "Slug already exists");
            }
            return;
        }

        if (articleRepository.existsBySlugAndIdNot(form.getSlug(), form.getId())) {
            bindingResult.rejectValue("slug", "duplicate", "Slug already exists");
        }
    }

    @Transactional
    public Article createArticle(ArticleForm form, MultipartFile file, String username) {
        if (username == null) {
            throw new InvalidOperationException("Authenticated user is required");
        }

        Article article = new Article();
        article.setAuthor(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found for username: " + username)));
        mapFormToArticle(article, form, file);
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Long id, ArticleForm form, MultipartFile file) {
        Article article = getArticle(id);
        mapFormToArticle(article, form, file);
        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.delete(getArticle(id));
    }

    private Article getArticle(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
    }

    private void mapFormToArticle(Article article, ArticleForm form, MultipartFile file) {
        article.setTitle(form.getTitle().trim());
        article.setSlug(form.getSlug().trim());
        article.setSummary(form.getSummary().trim());
        article.setContent(form.getContent().trim());
        article.setCategory(categoryService.getById(form.getCategoryId()));

        ArticleStatus status = ArticleStatus.valueOf(form.getStatus());
        article.setStatus(status);
        article.setReviewNote(normalize(form.getReviewNote()));

        if (status == ArticleStatus.PUBLISHED) {
            if (article.getPublishedAt() == null) {
                article.setPublishedAt(LocalDateTime.now());
            }
            article.setReviewNote(null);
        } else {
            article.setPublishedAt(null);
        }

        String thumbnailUrl = storageService.store(file);
        if (thumbnailUrl != null) {
            article.setThumbnailUrl(thumbnailUrl);
        }
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
