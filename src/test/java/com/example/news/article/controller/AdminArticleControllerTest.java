package com.example.news.article.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.example.news.article.service.ArticleManagementService;
import com.example.news.category.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class AdminArticleControllerTest {

    @Mock
    private ArticleManagementService articleManagementService;

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AdminArticleController(articleManagementService, categoryService))
                .setValidator(validator)
                .build();
    }

    @Test
    void saveArticleShouldRedirectWhenFormIsValid() throws Exception {        MockMultipartFile file = new MockMultipartFile("file", "thumb.png", "image/png", "img".getBytes());
        Principal principal = () -> "admin";

        mockMvc.perform(multipart("/admin/article/save")
                        .file(file)
                        .param("title", "New article")
                        .param("slug", "new-article")
                        .param("summary", "Short summary")
                        .param("content", "<p>Body</p>")
                        .param("categoryId", "1")
                        .param("status", "PUBLISHED")
                        .principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(articleManagementService).createArticle(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq("admin"));
    }
}


