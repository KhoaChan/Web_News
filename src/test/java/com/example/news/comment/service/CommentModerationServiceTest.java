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

import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.common.exception.InvalidOperationException;

@ExtendWith(MockitoExtension.class)
class CommentModerationServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentModerationService commentModerationService;

    @Test
    void approveCommentShouldMovePendingCommentToApproved() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setStatus(CommentStatus.PENDING);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment approvedComment = commentModerationService.approveComment(1L);

        assertThat(approvedComment.getStatus()).isEqualTo(CommentStatus.APPROVED);
    }

    @Test
    void rejectCommentShouldFailWhenCommentIsNotPending() {
        Comment comment = new Comment();
        comment.setId(2L);
        comment.setStatus(CommentStatus.APPROVED);

        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentModerationService.rejectComment(2L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("chờ duyệt");
    }
}
