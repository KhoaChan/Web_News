package com.example.news.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.article.web.ArticleForm;
import com.example.news.category.entity.Category;
import com.example.news.category.service.CategoryService;
import com.example.news.common.storage.StorageService;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ArticleManagementServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ArticleManagementService articleManagementService;

    @Test
    void createArticleShouldMapFormAndPersist() {
        ArticleForm form = new ArticleForm();
        form.setTitle("New title");
        form.setSlug("new-title");
        form.setSummary("New summary");
        form.setContent("<p>Body</p>");
        form.setCategoryId(1L);
        form.setStatus("DRAFT");

        Category category = new Category();
        category.setId(1L);

        User user = new User();
        user.setUsername("admin");

        MockMultipartFile file = new MockMultipartFile("file", "thumb.png", "image/png", "123".getBytes());

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(categoryService.getById(1L)).thenReturn(category);
        when(storageService.store(file)).thenReturn("/uploads/thumb.png");
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article savedArticle = articleManagementService.createArticle(form, file, "admin");

        assertThat(savedArticle.getTitle()).isEqualTo("New title");
        assertThat(savedArticle.getSlug()).isEqualTo("new-title");
        assertThat(savedArticle.getSummary()).isEqualTo("New summary");
        assertThat(savedArticle.getContent()).isEqualTo("<p>Body</p>");
        assertThat(savedArticle.getStatus()).isEqualTo(ArticleStatus.DRAFT);
        assertThat(savedArticle.getCategory()).isSameAs(category);
        assertThat(savedArticle.getAuthor()).isSameAs(user);
        assertThat(savedArticle.getThumbnailUrl()).isEqualTo("/uploads/thumb.png");
    }

    @Test
    void validateArticleFormShouldRejectDuplicateSlug() {
        ArticleForm form = new ArticleForm();
        form.setTitle("Title");
        form.setSlug("duplicate-slug");
        form.setSummary("Summary");
        form.setContent("<p>Body</p>");
        form.setCategoryId(1L);
        form.setStatus("PUBLISHED");

        Category category = new Category();
        category.setId(1L);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "articleForm");

        when(categoryService.getById(1L)).thenReturn(category);
        when(articleRepository.existsBySlug("duplicate-slug")).thenReturn(true);

        articleManagementService.validateArticleForm(form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("slug")).isTrue();
    }
}
