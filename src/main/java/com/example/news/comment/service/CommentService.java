package com.example.news.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.comment.entity.Comment;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.comment.web.CommentForm;
import com.example.news.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public Comment createComment(CommentForm form) {
        var article = articleRepository.findById(form.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + form.getArticleId()));

        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Article is not available for comments");
        }

        Comment comment = new Comment();
        comment.setCommenterName(form.getName().trim());
        comment.setContent(form.getContent().trim());
        comment.setArticle(article);
        return commentRepository.save(comment);
    }
}
