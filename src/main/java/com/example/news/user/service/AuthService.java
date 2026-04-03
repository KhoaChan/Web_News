package com.example.news.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.RegisterForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterForm buildRegisterForm() {
        return new RegisterForm();
    }

    public void validateRegistrationForm(RegisterForm form, BindingResult bindingResult) {
        String username = normalize(form.getUsername());
        String email = normalize(form.getEmail());

        if (StringUtils.hasText(form.getPassword()) && StringUtils.hasText(form.getConfirmPassword())
                && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Password confirmation does not match");
        }

        if (StringUtils.hasText(username) && userRepository.existsByUsername(username)) {
            bindingResult.rejectValue("username", "duplicate", "Username already exists");
        }

        if (StringUtils.hasText(email) && userRepository.existsByEmail(email)) {
            bindingResult.rejectValue("email", "duplicate", "Email already exists");
        }
    }

    public User registerUser(RegisterForm form) {
        User user = new User();
        user.setUsername(form.getUsername().trim());
        user.setEmail(form.getEmail().trim());
        user.setFullName(normalize(form.getFullName()));
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
