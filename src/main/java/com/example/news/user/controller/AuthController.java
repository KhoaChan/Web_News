package com.example.news.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.news.user.security.NewsUserPrincipal;
import com.example.news.user.service.AuthService;
import com.example.news.user.web.RegisterForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            Model model) {
        if (principal != null) {
            return principal.isAdmin() ? "redirect:/admin" : "redirect:/";
        }

        model.addAttribute("loginError", error != null);
        model.addAttribute("logoutSuccess", logout != null);
        model.addAttribute("registeredSuccess", registered != null);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @ModelAttribute("registerForm") RegisterForm registerForm) {
        if (principal != null) {
            return principal.isAdmin() ? "redirect:/admin" : "redirect:/";
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @AuthenticationPrincipal NewsUserPrincipal principal,
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
            BindingResult bindingResult) {
        if (principal != null) {
            return principal.isAdmin() ? "redirect:/admin" : "redirect:/";
        }

        authService.validateRegistrationForm(registerForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        authService.registerUser(registerForm);
        return "redirect:/login?registered";
    }
}
