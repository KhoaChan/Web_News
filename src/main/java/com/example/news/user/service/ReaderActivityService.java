package com.example.news.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.comment.entity.Comment;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.SavedArticle;
import com.example.news.user.entity.User;
import com.example.news.user.entity.ViewedArticle;
import com.example.news.user.repository.SavedArticleRepository;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.repository.ViewedArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReaderActivityService {

    private final SavedArticleRepository savedArticleRepository;
    private final ViewedArticleRepository viewedArticleRepository;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public List<Comment> findUserComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long countUserComments(Long userId) {
        return commentRepository.countByUserId(userId);
    }

    public List<SavedArticle> findSavedArticles(Long userId) {
        return savedArticleRepository.findByUserIdAndArticleStatusOrderBySavedAtDesc(userId, ArticleStatus.PUBLISHED);
    }

    public List<ViewedArticle> findViewedArticles(Long userId) {
        return viewedArticleRepository.findByUserIdAndArticleStatusOrderByViewedAtDesc(userId, ArticleStatus.PUBLISHED);
    }

    public boolean isArticleSaved(Long userId, Long articleId) {
        return savedArticleRepository.existsByUserIdAndArticleId(userId, articleId);
    }

    @Transactional
    public boolean toggleSavedArticle(Long userId, Long articleId) {
        SavedArticle existing = savedArticleRepository.findByUserIdAndArticleId(userId, articleId).orElse(null);
        if (existing != null) {
            savedArticleRepository.delete(existing);
            return false;
        }

        SavedArticle savedArticle = new SavedArticle();
        savedArticle.setUser(getUser(userId));
        savedArticle.setArticle(getPublishedArticle(articleId));
        savedArticleRepository.save(savedArticle);
        return true;
    }

    @Transactional
    public void recordViewedArticle(Long userId, Long articleId) {
        ViewedArticle viewedArticle = viewedArticleRepository.findByUserIdAndArticleId(userId, articleId).orElseGet(() -> {
            ViewedArticle created = new ViewedArticle();
            created.setUser(getUser(userId));
            created.setArticle(getPublishedArticle(articleId));
            return created;
        });
        viewedArticle.setViewedAt(LocalDateTime.now());
        viewedArticleRepository.save(viewedArticle);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

    private Article getPublishedArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với ID: " + articleId));
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Bài viết hiện chưa sẵn sàng để hiển thị công khai");
        }
        return article;
    }
}
