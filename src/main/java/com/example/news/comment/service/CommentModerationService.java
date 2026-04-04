package com.example.news.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentModerationService {

    private final CommentRepository commentRepository;

    public List<Comment> findPendingComments() {
        return commentRepository.findByStatusOrderByCreatedAtDesc(CommentStatus.PENDING);
    }

    public long countPendingComments() {
        return commentRepository.countByStatus(CommentStatus.PENDING);
    }

    @Transactional
    public Comment approveComment(Long id) {
        Comment comment = getPendingComment(id);
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment rejectComment(Long id) {
        Comment comment = getPendingComment(id);
        comment.setStatus(CommentStatus.REJECTED);
        return commentRepository.save(comment);
    }

    private Comment getPendingComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận với ID: " + id));
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new InvalidOperationException("Bình luận này không còn ở trạng thái chờ duyệt");
        }
        return comment;
    }
}
