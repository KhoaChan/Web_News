package com.example.news.component;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.news.entity.Article;
import com.example.news.entity.Category;
import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.repository.ArticleRepository;
import com.example.news.repository.CategoryRepository;
import com.example.news.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Thêm Chuyên mục
        if (categoryRepository.count() == 0) {
            Category theThao = new Category(null, "Thể Thao", "the-thao", "Tin tức thể thao", null);
            Category congNghe = new Category(null, "Công Nghệ", "cong-nghe", "Công nghệ mới nhất", null);
            Category giaiTri = new Category(null, "Giải Trí", "giai-tri", "Showbiz, phim ảnh", null);
            categoryRepository.saveAll(Arrays.asList(theThao, congNghe, giaiTri));
            System.out.println("✅ Đã tạo dữ liệu mẫu cho Chuyên mục!");
        }

        // 2. Thêm User Admin
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEmail("admin@news.com");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Đã tạo tài khoản Admin mặc định!");
        }

        // 3. Thêm Bài viết mẫu
        if (articleRepository.count() == 0) {
            // Lấy lại admin và chuyên mục từ DB để gán vào bài viết
            User author = userRepository.findByUsername("admin").orElseThrow();
            Category theThao = categoryRepository.findBySlug("the-thao").orElseThrow();
            Category congNghe = categoryRepository.findBySlug("cong-nghe").orElseThrow();

            Article a1 = new Article();
            a1.setTitle("Đội tuyển Việt Nam chuẩn bị cho giải đấu lớn");
            a1.setSlug("doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon");
            a1.setSummary("Các cầu thủ đang tập luyện hăng say chờ ngày ra trận...");
            a1.setContent("Nội dung chi tiết của bài báo thể thao này sẽ rất dài, sau này chúng ta sẽ dùng trình soạn thảo để chèn thêm ảnh và định dạng chữ.");
            a1.setCategory(theThao);
            a1.setAuthor(author);
            a1.setStatus("PUBLISHED");

            Article a2 = new Article();
            a2.setTitle("Ra mắt dòng Laptop Gaming thế hệ mới cực khủng");
            a2.setSlug("ra-mat-dong-laptop-gaming-the-he-moi");
            a2.setSummary("Sở hữu card đồ họa mới nhất cùng hệ thống tản nhiệt tiên tiến...");
            a2.setContent("Nội dung chi tiết đánh giá về hiệu năng, màn hình và trải nghiệm chơi game...");
            a2.setCategory(congNghe);
            a2.setAuthor(author);
            a2.setStatus("PUBLISHED");

            articleRepository.saveAll(Arrays.asList(a1, a2));
            System.out.println("✅ Đã tạo dữ liệu mẫu cho Bài viết!");
        }
    }
}