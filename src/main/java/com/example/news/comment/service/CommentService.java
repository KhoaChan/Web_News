package com.example.news.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.comment.entity.Comment;
import com.example.news.comment.entity.CommentStatus;
import com.example.news.comment.repository.CommentRepository;
import com.example.news.comment.web.CommentForm;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment createComment(CommentForm form, Long userId) {
        var article = articleRepository.findById(form.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với ID: " + form.getArticleId()));

        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Bài viết hiện không nhận bình luận");
        }

        Comment comment = new Comment();
        comment.setContent(form.getContent().trim());
        comment.setStatus(CommentStatus.PENDING);
        comment.setArticle(article);

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
            comment.setUser(user);
            comment.setCommenterName(resolveCommenterName(user));
        } else {
            comment.setCommenterName(form.getName().trim());
        }

        return commentRepository.save(comment);
    }

    public List<Comment> findApprovedCommentsForArticle(Long articleId) {
        return commentRepository.findByArticleIdAndStatusOrderByCreatedAtDesc(articleId, CommentStatus.APPROVED);
    }

    public long countApprovedCommentsForArticle(Long articleId) {
        return commentRepository.countByArticleIdAndStatus(articleId, CommentStatus.APPROVED);
    }

    private String resolveCommenterName(User user) {
        if (StringUtils.hasText(user.getFullName())) {
            return user.getFullName().trim();
        }
        return user.getUsername();
    }
}
