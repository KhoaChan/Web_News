package com.example.news.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleIdAndStatusOrderByCreatedAtDesc(Long articleId, CommentStatus status);

    long countByArticleIdAndStatus(Long articleId, CommentStatus status);

    List<Comment> findByStatusOrderByCreatedAtDesc(CommentStatus status);

    long countByStatus(CommentStatus status);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);
}
