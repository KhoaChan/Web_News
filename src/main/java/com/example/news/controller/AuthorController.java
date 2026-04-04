package com.example.news.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.entity.Article;
import com.example.news.entity.User;
import com.example.news.repository.UserRepository;
import com.example.news.service.ArticleService;
import com.example.news.service.CategoryService;
import com.example.news.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final FileUploadService fileUploadService;
    private final UserRepository userRepository;

    // 1. Hiển thị form viết bài
    @GetMapping("/write")
    public String writeArticle(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.findAll());
        return "author/write"; 
    }

    // 2. Nhận dữ liệu bài viết và lưu vào Database
    @PostMapping("/write")
    public String saveArticle(@ModelAttribute("article") Article formArticle,
                              @RequestParam("file") MultipartFile file,
                              Principal principal) throws IOException {
        
        // Nhận diện người đang viết bài
        String username = principal.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        
        // Gán tác giả và đặt trạng thái là "PENDING" (Chờ duyệt)
        formArticle.setAuthor(author);
        formArticle.setStatus("PENDING"); 

        // Xử lý Upload Ảnh lên Cloudinary
        if (!file.isEmpty()) {
            String imageUrl = fileUploadService.uploadFile(file);
            formArticle.setThumbnailUrl(imageUrl);
        }

        // Lưu vào DB
        articleService.save(formArticle);
        
        // Nộp xong thì đá về trang chủ kèm theo 1 biến message để thông báo
        return "redirect:/?message=posted"; 
    }
}