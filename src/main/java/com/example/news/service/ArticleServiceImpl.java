package com.example.news.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.news.entity.Article;
import com.example.news.entity.Comment; // Nhớ import Entity Comment
import com.example.news.repository.ArticleRepository;
import com.example.news.repository.CommentRepository; // Nhớ import Repository này

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository; // Đã thêm để hết lỗi đỏ!

    @Override
    public Page<Article> findAll(int page, int size) {
        // Sắp xếp theo ngày tạo (createdAt) giảm dần để tin mới nhất hiện lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, "PUBLISHED", pageable);
    }

    @Override
    public Page<Article> findByCategory(String slug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleRepository.findByCategorySlugAndStatus(slug, "PUBLISHED", pageable);
    }

    @Override
    public Article findBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với slug: " + slug));
    }

    @Override
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
    }

    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public void saveComment(Long articleId, String name, String content) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết để bình luận"));
        
        Comment comment = new Comment();
        comment.setCommenterName(name);
        comment.setContent(content);
        comment.setArticle(article);
        
        commentRepository.save(comment); 
    }
}