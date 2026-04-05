package com.example.news.article.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.service.ArticleQueryService;
import com.example.news.category.entity.Category;
import com.example.news.category.service.CategoryService;
import com.example.news.comment.service.CommentService;
import com.example.news.common.web.GlobalExceptionHandler;
import com.example.news.user.service.ReaderActivityService;

@ExtendWith(MockitoExtension.class)
class PublicArticleControllerTest {

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CommentService commentService;

    @Mock
    private ReaderActivityService readerActivityService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PublicArticleController(articleQueryService, categoryService, commentService, readerActivityService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void postCommentWithValidationErrorsShouldReturnDetailView() throws Exception {
        Article article = new Article();
        article.setId(1L);
        article.setSlug("sample-article");
        article.setStatus(ArticleStatus.PUBLISHED);

        Category category = new Category();
        category.setName("Tech");
        category.setSlug("tech");
        article.setCategory(category);

        when(articleQueryService.getPublishedArticleById(1L)).thenReturn(article);
        when(categoryService.findAll()).thenReturn(List.of(category));
        when(commentService.findApprovedCommentsForArticle(1L)).thenReturn(List.of());
        when(commentService.countApprovedCommentsForArticle(1L)).thenReturn(0L);

        mockMvc.perform(post("/article/comment")
                        .param("articleId", "1")
                        .param("name", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attributeExists("article", "categories", "approvedComments", "approvedCommentCount"))
                .andExpect(model().attribute("savedArticle", false));

        verify(commentService, never()).createComment(any(), any());
        verify(readerActivityService, never()).isArticleSaved(any(), any());
    }

    @Test
    void postCommentShouldCreateAnonymousCommentWhenFormValid() throws Exception {
        Article article = new Article();
        article.setId(1L);
        article.setSlug("sample-article");
        article.setStatus(ArticleStatus.PUBLISHED);

        when(articleQueryService.getPublishedArticleById(1L)).thenReturn(article);

        mockMvc.perform(post("/article/comment")
                        .param("articleId", "1")
                        .param("name", "Lan")
                        .param("content", "Bình luận hợp lệ"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article/sample-article"));

        verify(commentService).createComment(any(), eq(null));
    }
}
