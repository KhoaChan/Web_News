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
        Category theThao = ensureCategory("Thể thao", "the-thao", "Tin tức thể thao");
        Category congNghe = ensureCategory("Công nghệ", "cong-nghe", "Tin tức công nghệ mới nhất");
        ensureCategory("Giải trí", "giai-tri", "Tin tức showbiz và phim ảnh");

        User admin = ensureUser("admin", "admin@news.com", "Quản trị hệ thống", Role.ADMIN);
        User author = ensureUser("author", "author@news.com", "Tác giả demo", Role.AUTHOR);
        ensureUser("editor", "editor@news.com", "Biên tập viên demo", Role.EDITOR);

        ensureArticle(
                "doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon",
                "Đội tuyển Việt Nam chuẩn bị cho giải đấu lớn",
                "Các cầu thủ đang tập luyện hăng say cho giải đấu lớn sắp tới.",
                "<p>Nội dung chi tiết của bài báo thể thao mẫu.</p>",
                theThao,
                admin);

        ensureArticle(
                "ra-mat-dong-laptop-gaming-the-he-moi",
                "Ra mắt dòng laptop gaming thế hệ mới",
                "Sở hữu cấu hình mạnh và hệ thống tản nhiệt cải tiến.",
                "<p>Nội dung chi tiết của bài báo công nghệ mẫu.</p>",
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
