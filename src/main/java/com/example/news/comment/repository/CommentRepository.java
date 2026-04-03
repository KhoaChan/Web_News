package com.example.news.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
