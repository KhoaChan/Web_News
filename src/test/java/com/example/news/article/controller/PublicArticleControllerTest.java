package com.example.news.article.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class PublicArticleControllerTest {

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PublicArticleController(articleQueryService, categoryService, commentService))
                .setControllerAdvice(new GlobalExceptionHandler())
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
        article.setCategory(category);

        when(articleQueryService.getPublishedArticleById(1L)).thenReturn(article);
        when(categoryService.findAll()).thenReturn(List.of(category));

        mockMvc.perform(post("/article/comment")
                        .param("articleId", "1")
                        .param("name", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attributeExists("article", "categories"));

        verify(commentService, never()).createComment(org.mockito.ArgumentMatchers.any());
    }
}
