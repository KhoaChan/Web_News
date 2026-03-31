package com.example.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.news.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}