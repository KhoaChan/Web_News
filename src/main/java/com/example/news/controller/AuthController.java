package com.example.news.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Trả về giao diện Đăng nhập
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Trả về giao diện Đăng ký
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Xử lý logic Đăng ký tài khoản mới (Mặc định là Cấp 1)
    @PostMapping("/register")
    public String registerUser(@RequestParam String email, 
                               @RequestParam String username, 
                               @RequestParam String password, 
                               Model model) {
        
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }

        // Tạo tài khoản mới
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(Role.USER); // Mặc định cấp 1 là USER

        userRepository.save(newUser);

        return "redirect:/login?success=true";
    }
}