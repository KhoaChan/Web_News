package com.example.news.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
List<Comment> findByArticleIdAndStatusOrderByCreatedAtDesc(Long articleId, String status);

List<Comment> findByStatusOrderByCreatedAtDesc(String status);
}