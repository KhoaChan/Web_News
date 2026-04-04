package com.example.news.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với ID: " + form.getArticleId()));

        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Bài viết hiện không nhận bình luận");
        }

        Comment comment = new Comment();
        comment.setCommenterName(form.getName().trim());
        comment.setContent(form.getContent().trim());
        comment.setStatus(CommentStatus.PENDING);
        comment.setArticle(article);
        return commentRepository.save(comment);
    }

    public List<Comment> findApprovedCommentsForArticle(Long articleId) {
        return commentRepository.findByArticleIdAndStatusOrderByCreatedAtDesc(articleId, CommentStatus.APPROVED);
    }

    public long countApprovedCommentsForArticle(Long articleId) {
        return commentRepository.countByArticleIdAndStatus(articleId, CommentStatus.APPROVED);
    }
}
