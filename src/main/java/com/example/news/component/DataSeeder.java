package com.example.news.component;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.news.entity.Category;
import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.repository.CategoryRepository;
import com.example.news.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
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
    }
}