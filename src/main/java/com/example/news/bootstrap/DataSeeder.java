package com.example.news.bootstrap;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
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
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            var theThao = new Category(null, "The thao", "the-thao", "Tin tuc the thao", null);
            var congNghe = new Category(null, "Cong nghe", "cong-nghe", "Cong nghe moi nhat", null);
            var giaiTri = new Category(null, "Giai tri", "giai-tri", "Showbiz va phim anh", null);
            categoryRepository.saveAll(Arrays.asList(theThao, congNghe, giaiTri));
        }

        if (userRepository.count() == 0) {
            var admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEmail("admin@news.com");
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        }

        if (articleRepository.count() == 0) {
            var author = userRepository.findByUsername("admin").orElseThrow();
            var theThao = categoryRepository.findBySlug("the-thao").orElseThrow();
            var congNghe = categoryRepository.findBySlug("cong-nghe").orElseThrow();

            var article1 = new Article();
            article1.setTitle("Doi tuyen Viet Nam chuan bi cho giai dau lon");
            article1.setSlug("doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon");
            article1.setSummary("Cac cau thu dang tap luyen hang say cho giai dau lon sap toi.");
            article1.setContent("<p>Noi dung chi tiet cua bai bao the thao mau.</p>");
            article1.setCategory(theThao);
            article1.setAuthor(author);
            article1.setStatus(ArticleStatus.PUBLISHED);

            var article2 = new Article();
            article2.setTitle("Ra mat dong laptop gaming the he moi");
            article2.setSlug("ra-mat-dong-laptop-gaming-the-he-moi");
            article2.setSummary("So huu cau hinh manh va he thong tan nhiet cai tien.");
            article2.setContent("<p>Noi dung chi tiet cua bai bao cong nghe mau.</p>");
            article2.setCategory(congNghe);
            article2.setAuthor(author);
            article2.setStatus(ArticleStatus.PUBLISHED);

            articleRepository.saveAll(Arrays.asList(article1, article2));
        }
    }
}
