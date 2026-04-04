package com.example.news.bootstrap;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;
import com.example.news.category.entity.Category;
import com.example.news.category.repository.CategoryRepository;
import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Category theThao = ensureCategory("The thao", "the-thao", "Tin tuc the thao");
        Category congNghe = ensureCategory("Cong nghe", "cong-nghe", "Cong nghe moi nhat");
        ensureCategory("Giai tri", "giai-tri", "Showbiz va phim anh");

        User admin = ensureUser("admin", "admin@news.com", "System Administrator", Role.ADMIN);
        User author = ensureUser("author", "author@news.com", "Demo Author", Role.AUTHOR);
        ensureUser("editor", "editor@news.com", "Demo Editor", Role.EDITOR);

        ensureArticle(
                "doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon",
                "Doi tuyen Viet Nam chuan bi cho giai dau lon",
                "Cac cau thu dang tap luyen hang say cho giai dau lon sap toi.",
                "<p>Noi dung chi tiet cua bai bao the thao mau.</p>",
                theThao,
                admin);

        ensureArticle(
                "ra-mat-dong-laptop-gaming-the-he-moi",
                "Ra mat dong laptop gaming the he moi",
                "So huu cau hinh manh va he thong tan nhiet cai tien.",
                "<p>Noi dung chi tiet cua bai bao cong nghe mau.</p>",
                congNghe,
                author);
    }

    private Category ensureCategory(String name, String slug, String description) {
        return categoryRepository.findBySlug(slug).orElseGet(() -> {
            Category category = new Category();
            category.setName(name);
            category.setSlug(slug);
            category.setDescription(description);
            return categoryRepository.save(category);
        });
    }

    private User ensureUser(String username, String email, String fullName, Role role) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(role);
            user.setEnabled(true);
            return userRepository.save(user);
        });
    }

    private void ensureArticle(String slug, String title, String summary, String content, Category category, User author) {
        if (articleRepository.findBySlug(slug).isPresent()) {
            return;
        }

        Article article = new Article();
        article.setTitle(title);
        article.setSlug(slug);
        article.setSummary(summary);
        article.setContent(content);
        article.setCategory(category);
        article.setAuthor(author);
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        articleRepository.saveAll(Arrays.asList(article));
    }
}
