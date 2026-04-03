package com.example.news.category.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.news.article.repository.ArticleRepository;
import com.example.news.category.entity.Category;
import com.example.news.category.repository.CategoryRepository;
import com.example.news.common.exception.InvalidOperationException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void deleteCategoryShouldRejectWhenArticlesStillExist() {
        Category category = new Category();
        category.setId(10L);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(articleRepository.existsByCategoryId(10L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(10L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("still has articles");

        verify(categoryRepository, never()).delete(category);
    }
}
