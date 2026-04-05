package com.example.news.comment.service;

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
import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.comment.web.CommentForm;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentShouldPersistPendingCommentForPublishedArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setStatus(ArticleStatus.PUBLISHED);

        CommentForm form = new CommentForm();
        form.setArticleId(1L);
        form.setName("Lan");
        form.setContent("Bài viết rất hay");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment savedComment = commentService.createComment(form, null);

        assertThat(savedComment.getStatus()).isEqualTo(CommentStatus.PENDING);
        assertThat(savedComment.getArticle()).isSameAs(article);
        assertThat(savedComment.getCommenterName()).isEqualTo("Lan");
        assertThat(savedComment.getUser()).isNull();
    }

    @Test
    void createCommentShouldAttachAuthenticatedUserWhenProvided() {
        Article article = new Article();
        article.setId(3L);
        article.setStatus(ArticleStatus.PUBLISHED);

        User user = new User();
        user.setId(9L);
        user.setUsername("reader");
        user.setFullName("Reader Account");
        user.setRole(Role.USER);

        CommentForm form = new CommentForm();
        form.setArticleId(3L);
        form.setName("Should be ignored");
        form.setContent("Bài viết rất hay");

        when(articleRepository.findById(3L)).thenReturn(Optional.of(article));
        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment savedComment = commentService.createComment(form, 9L);

        assertThat(savedComment.getUser()).isSameAs(user);
        assertThat(savedComment.getCommenterName()).isEqualTo("Reader Account");
    }

    @Test
    void createCommentShouldRejectNonPublishedArticle() {
        Article article = new Article();
        article.setId(2L);
        article.setStatus(ArticleStatus.DRAFT);

        CommentForm form = new CommentForm();
        form.setArticleId(2L);
        form.setName("Lan");
        form.setContent("Bài viết rất hay");

        when(articleRepository.findById(2L)).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> commentService.createComment(form, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("không nhận bình luận");
    }
}
