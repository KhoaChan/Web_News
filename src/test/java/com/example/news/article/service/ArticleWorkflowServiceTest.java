package com.example.news.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.category.service.CategoryService;
import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.storage.StorageService;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ArticleWorkflowServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ArticleWorkflowService articleWorkflowService;

    @Test
    void submitForReviewShouldMoveDraftArticleToInReview() {
        Article article = new Article();
        article.setId(1L);
        article.setStatus(ArticleStatus.DRAFT);
        article.setAuthor(author("alice"));

        when(articleRepository.findByIdAndAuthorUsername(1L, "alice")).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article savedArticle = articleWorkflowService.submitForReview(1L, "alice");

        assertThat(savedArticle.getStatus()).isEqualTo(ArticleStatus.IN_REVIEW);
        assertThat(savedArticle.getReviewNote()).isNull();
    }

    @Test
    void publishShouldSetPublishedAtAndClearReviewNote() {
        Article article = new Article();
        article.setId(2L);
        article.setStatus(ArticleStatus.IN_REVIEW);
        article.setReviewNote("Needs cleanup");

        when(articleRepository.findById(2L)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article publishedArticle = articleWorkflowService.publish(2L);

        assertThat(publishedArticle.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
        assertThat(publishedArticle.getPublishedAt()).isNotNull();
        assertThat(publishedArticle.getReviewNote()).isNull();
    }

    @Test
    void requestChangesShouldSetChangesRequestedAndPersistNote() {
        Article article = new Article();
        article.setId(3L);
        article.setStatus(ArticleStatus.IN_REVIEW);

        when(articleRepository.findById(3L)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article updatedArticle = articleWorkflowService.requestChanges(3L, "Please update the title and summary.");

        assertThat(updatedArticle.getStatus()).isEqualTo(ArticleStatus.CHANGES_REQUESTED);
        assertThat(updatedArticle.getReviewNote()).isEqualTo("Please update the title and summary.");
        assertThat(updatedArticle.getPublishedAt()).isNull();
    }

    @Test
    void cancelByAuthorShouldRejectInReviewArticle() {
        Article article = new Article();
        article.setId(4L);
        article.setStatus(ArticleStatus.IN_REVIEW);
        article.setAuthor(author("alice"));

        when(articleRepository.findByIdAndAuthorUsername(4L, "alice")).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> articleWorkflowService.cancelByAuthor(4L, "alice"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("cancelled by the author");
    }

    private User author(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
