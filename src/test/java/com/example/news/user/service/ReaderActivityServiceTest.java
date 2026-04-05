package com.example.news.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.SavedArticle;
import com.example.news.user.entity.User;
import com.example.news.user.entity.ViewedArticle;
import com.example.news.user.repository.SavedArticleRepository;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.repository.ViewedArticleRepository;

@ExtendWith(MockitoExtension.class)
class ReaderActivityServiceTest {

    @Mock
    private SavedArticleRepository savedArticleRepository;

    @Mock
    private ViewedArticleRepository viewedArticleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReaderActivityService readerActivityService;

    @Test
    void toggleSavedArticleShouldCreateNewSaveWhenNotExists() {
        User user = new User();
        user.setId(1L);

        Article article = new Article();
        article.setId(2L);
        article.setStatus(ArticleStatus.PUBLISHED);

        when(savedArticleRepository.findByUserIdAndArticleId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(articleRepository.findById(2L)).thenReturn(Optional.of(article));

        boolean saved = readerActivityService.toggleSavedArticle(1L, 2L);

        assertThat(saved).isTrue();
        verify(savedArticleRepository).save(any(SavedArticle.class));
    }

    @Test
    void toggleSavedArticleShouldRemoveExistingSave() {
        SavedArticle existing = new SavedArticle();
        existing.setId(5L);

        when(savedArticleRepository.findByUserIdAndArticleId(1L, 2L)).thenReturn(Optional.of(existing));

        boolean saved = readerActivityService.toggleSavedArticle(1L, 2L);

        assertThat(saved).isFalse();
        verify(savedArticleRepository).delete(existing);
        verify(savedArticleRepository, never()).save(any(SavedArticle.class));
    }

    @Test
    void toggleSavedArticleShouldRejectUnpublishedArticle() {
        User user = new User();
        user.setId(1L);

        Article article = new Article();
        article.setId(2L);
        article.setStatus(ArticleStatus.DRAFT);

        when(savedArticleRepository.findByUserIdAndArticleId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(articleRepository.findById(2L)).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> readerActivityService.toggleSavedArticle(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("chưa sẵn sàng");
    }

    @Test
    void recordViewedArticleShouldUpdateTimestampForExistingEntry() {
        ViewedArticle existing = new ViewedArticle();
        existing.setId(6L);
        existing.setViewedAt(LocalDateTime.now().minusDays(1));

        when(viewedArticleRepository.findByUserIdAndArticleId(1L, 2L)).thenReturn(Optional.of(existing));

        readerActivityService.recordViewedArticle(1L, 2L);

        verify(viewedArticleRepository).save(existing);
    }

    @Test
    void recordViewedArticleShouldCreateEntryWhenMissing() {
        User user = new User();
        user.setId(1L);

        Article article = new Article();
        article.setId(2L);
        article.setStatus(ArticleStatus.PUBLISHED);

        when(viewedArticleRepository.findByUserIdAndArticleId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(articleRepository.findById(2L)).thenReturn(Optional.of(article));

        readerActivityService.recordViewedArticle(1L, 2L);

        verify(viewedArticleRepository).save(any(ViewedArticle.class));
    }
}
