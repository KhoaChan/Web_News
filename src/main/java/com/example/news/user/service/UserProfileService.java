package com.example.news.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.ChangePasswordForm;
import com.example.news.user.web.UserProfileForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

    public UserProfileForm getProfileForm(Long userId) {
        User user = getUser(userId);
        UserProfileForm form = new UserProfileForm();
        form.setEmail(user.getEmail());
        form.setFullName(user.getFullName());
        return form;
    }

    public ChangePasswordForm buildChangePasswordForm() {
        return new ChangePasswordForm();
    }

    public void validateProfileForm(Long userId, UserProfileForm form, BindingResult bindingResult) {
        String email = normalize(form.getEmail());
        if (!StringUtils.hasText(email)) {
            bindingResult.rejectValue("email", "required", "Vui lòng nhập email");
            return;
        }

        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
        }
    }

    public User updateProfile(Long userId, UserProfileForm form) {
        User user = getUser(userId);
        user.setEmail(form.getEmail().trim());
        user.setFullName(normalize(form.getFullName()));
        return userRepository.save(user);
    }

    public void validateChangePasswordForm(Long userId, ChangePasswordForm form, BindingResult bindingResult) {
        User user = getUser(userId);

        if (StringUtils.hasText(form.getCurrentPassword()) && !passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            bindingResult.rejectValue("currentPassword", "invalid", "Mật khẩu hiện tại không đúng");
        }

        if (StringUtils.hasText(form.getNewPassword()) && StringUtils.hasText(form.getConfirmPassword())
                && !form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Xác nhận mật khẩu không khớp");
        }
    }

    public User changePassword(Long userId, ChangePasswordForm form) {
        User user = getUser(userId);
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        return userRepository.save(user);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
