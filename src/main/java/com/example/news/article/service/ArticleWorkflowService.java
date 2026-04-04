package com.example.news.article.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
public class ArticleWorkflowService {

    private static final EnumSet<ArticleStatus> AUTHOR_EDITABLE_STATUSES = EnumSet.of(
            ArticleStatus.DRAFT,
            ArticleStatus.CHANGES_REQUESTED);

    private final ArticleRepository articleRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public List<Article> findAuthorArticles(String username) {
        return articleRepository.findByAuthorUsernameOrderByCreatedAtDesc(username);
    }

    public List<Article> findEditorReviewQueue() {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.IN_REVIEW);
    }

    public List<Article> findEditorPublishedArticles() {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> findEditorCancelledArticles() {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.CANCELLED);
    }

    public ArticleForm buildAuthorForm() {
        ArticleForm form = new ArticleForm();
        form.setStatus(ArticleStatus.DRAFT.name());
        return form;
    }

    public ArticleForm getAuthorArticleForm(Long id, String username) {
        Article article = getEditableAuthorArticle(id, username);
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

    public Article getArticleForEditorReview(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với ID: " + id));
    }

    @Transactional
    public Article createAuthorArticle(ArticleForm form, MultipartFile file, String username) {
        Article article = new Article();
        article.setAuthor(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với tên đăng nhập: " + username)));
        article.setStatus(ArticleStatus.DRAFT);
        article.setReviewNote(null);
        mapAuthorFields(article, form, file);
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateAuthorArticle(Long id, ArticleForm form, MultipartFile file, String username) {
        Article article = getEditableAuthorArticle(id, username);
        mapAuthorFields(article, form, file);
        return articleRepository.save(article);
    }

    @Transactional
    public Article submitForReview(Long id, String username) {
        Article article = getOwnedArticle(id, username);
        if (article.getStatus() != ArticleStatus.DRAFT && article.getStatus() != ArticleStatus.CHANGES_REQUESTED) {
            throw new InvalidOperationException("Chỉ bài viết ở trạng thái bản nháp hoặc cần chỉnh sửa mới có thể gửi duyệt");
        }
        article.setStatus(ArticleStatus.IN_REVIEW);
        article.setReviewNote(null);
        return articleRepository.save(article);
    }

    @Transactional
    public Article cancelByAuthor(Long id, String username) {
        Article article = getOwnedArticle(id, username);
        if (article.getStatus() != ArticleStatus.DRAFT && article.getStatus() != ArticleStatus.CHANGES_REQUESTED) {
            throw new InvalidOperationException("Chỉ bài viết ở trạng thái bản nháp hoặc cần chỉnh sửa mới có thể bị tác giả hủy");
        }
        article.setStatus(ArticleStatus.CANCELLED);
        return articleRepository.save(article);
    }

    @Transactional
    public Article publish(Long id) {
        Article article = getArticleForEditorReview(id);
        if (article.getStatus() != ArticleStatus.IN_REVIEW) {
            throw new InvalidOperationException("Chỉ bài viết đang chờ duyệt mới có thể được xuất bản");
        }
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        article.setReviewNote(null);
        return articleRepository.save(article);
    }

    @Transactional
    public Article requestChanges(Long id, String reviewNote) {
        Article article = getArticleForEditorReview(id);
        if (article.getStatus() != ArticleStatus.IN_REVIEW) {
            throw new InvalidOperationException("Chỉ bài viết đang chờ duyệt mới có thể bị trả về để chỉnh sửa");
        }
        article.setStatus(ArticleStatus.CHANGES_REQUESTED);
        article.setReviewNote(normalize(reviewNote, "Vui lòng chỉnh sửa bài viết này và gửi duyệt lại."));
        article.setPublishedAt(null);
        return articleRepository.save(article);
    }

    @Transactional
    public Article cancelByEditor(Long id, String reviewNote) {
        Article article = getArticleForEditorReview(id);
        if (article.getStatus() != ArticleStatus.IN_REVIEW) {
            throw new InvalidOperationException("Chỉ bài viết đang chờ duyệt mới có thể bị biên tập viên hủy");
        }
        article.setStatus(ArticleStatus.CANCELLED);
        article.setReviewNote(normalize(reviewNote, "Bài gửi này đã bị hủy trong quá trình biên tập."));
        article.setPublishedAt(null);
        return articleRepository.save(article);
    }

    private Article getEditableAuthorArticle(Long id, String username) {
        Article article = getOwnedArticle(id, username);
        if (!AUTHOR_EDITABLE_STATUSES.contains(article.getStatus())) {
            throw new InvalidOperationException("Bài viết này không thể được tác giả chỉnh sửa ở trạng thái hiện tại");
        }
        return article;
    }

    private Article getOwnedArticle(Long id, String username) {
        return articleRepository.findByIdAndAuthorUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết thuộc tác giả hiện tại với ID: " + id));
    }

    private void mapAuthorFields(Article article, ArticleForm form, MultipartFile file) {
        article.setTitle(form.getTitle().trim());
        article.setSlug(form.getSlug().trim());
        article.setSummary(form.getSummary().trim());
        article.setContent(form.getContent().trim());
        article.setCategory(categoryService.getById(form.getCategoryId()));

        String thumbnailUrl = storageService.store(file);
        if (thumbnailUrl != null) {
            article.setThumbnailUrl(thumbnailUrl);
        }
    }

    private String normalize(String value, String defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return value.trim();
    }
}
