package com.example.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.news.entity.Comment;
import com.example.news.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    
    private final CommentRepository commentRepository;

    // Hiển thị danh sách bình luận đang CHỜ DUYỆT
    @GetMapping
    public String list(Model model) {
        model.addAttribute("comments", commentRepository.findByStatusOrderByCreatedAtDesc("PENDING"));
        return "admin/comments";
    }

    // Hàm Xử lý DUYỆT bình luận
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        Comment c = commentRepository.findById(id).orElseThrow();
        c.setStatus("APPROVED");
        commentRepository.save(c);
        return "redirect:/admin/comments";
    }

    // Hàm Xử lý XÓA (TỪ CHỐI) bình luận
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return "redirect:/admin/comments";
    }
}