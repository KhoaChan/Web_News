package com.example.news.article.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.category.entity.Category;
import com.example.news.category.repository.CategoryRepository;
import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.SavedArticleRepository;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.security.NewsUserPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
class PublicArticleSaveIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavedArticleRepository savedArticleRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User reader;
    private Article article;

    @BeforeEach
    void setUp() {
        tearDown();

        reader = new User();
        reader.setUsername("reader-save");
        reader.setPassword("secret");
        reader.setEmail("reader-save@example.com");
        reader.setFullName("Reader Save");
        reader.setRole(Role.USER);
        reader.setEnabled(true);
        reader = userRepository.save(reader);

        Category category = new Category();
        category.setName("Cong nghe save");
        category.setSlug("cong-nghe-save");
        category = categoryRepository.save(category);

        article = new Article();
        article.setTitle("Tin save");
        article.setSlug("tin-save");
        article.setSummary("Tom tat save");
        article.setContent("<p>Noi dung save</p>");
        article.setCategory(category);
        article.setAuthor(reader);
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        article = articleRepository.save(article);
    }

    @AfterEach
    void tearDown() {
        savedArticleRepository.deleteAll();
        articleRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void postSaveArticleShouldToggleSavedStateAndRedirectBack() throws Exception {
        String redirectPath = "/article/" + article.getSlug();

        mockMvc.perform(post("/article/save/{id}", article.getId())
                        .param("redirectTo", redirectPath)
                        .with(user(principal(reader))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectPath));

        assertThat(savedArticleRepository.existsByUserIdAndArticleId(reader.getId(), article.getId())).isTrue();

        mockMvc.perform(post("/article/save/{id}", article.getId())
                        .param("redirectTo", redirectPath)
                        .with(user(principal(reader))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectPath));

        assertThat(savedArticleRepository.existsByUserIdAndArticleId(reader.getId(), article.getId())).isFalse();
    }

    private NewsUserPrincipal principal(User user) {
        return new NewsUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                user.isEnabled());
    }
}
