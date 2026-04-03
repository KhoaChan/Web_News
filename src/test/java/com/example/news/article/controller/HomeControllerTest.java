package com.example.news.article.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;
import com.example.news.category.entity.Category;
import com.example.news.category.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ArticleQueryService articleQueryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(categoryService, articleQueryService)).build();
    }

    @Test
    void homeShouldPopulateIndexModel() throws Exception {
        Article article = new Article();
        article.setTitle("Headline");

        Category category = new Category();
        category.setName("Tech");

        when(articleQueryService.findPublishedArticles(0, 5)).thenReturn(new PageImpl<>(List.of(article)));
        when(categoryService.findAll()).thenReturn(List.of(category));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("articles", "categories", "currentPage", "totalPages"));
    }
}
