package com.example.news.user.service;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.common.storage.StorageService;
import com.example.news.user.entity.Gender;
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
    private final StorageService storageService;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

    public UserProfileForm getProfileForm(Long userId) {
        User user = getUser(userId);
        UserProfileForm form = new UserProfileForm();
        form.setEmail(user.getEmail());
        form.setFullName(user.getFullName());
        form.setBirthDate(user.getBirthDate());
        form.setGender(user.getGender() == null ? null : user.getGender().name());
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

        if (form.getBirthDate() != null && form.getBirthDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("birthDate", "future", "Ngày sinh không hợp lệ");
        }

        if (StringUtils.hasText(form.getGender())) {
            try {
                Gender.valueOf(form.getGender().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                bindingResult.rejectValue("gender", "invalid", "Giới tính không hợp lệ");
            }
        }
    }

    public User updateProfile(Long userId, UserProfileForm form) {
        User user = getUser(userId);
        user.setEmail(form.getEmail().trim());
        user.setFullName(normalize(form.getFullName()));
        user.setBirthDate(form.getBirthDate());
        user.setGender(parseGender(form.getGender()));
        return userRepository.save(user);
    }

    public User updateAvatar(Long userId, MultipartFile file) {
        validateAvatar(file);
        User user = getUser(userId);
        user.setAvatarUrl(storageService.store(file));
        return userRepository.save(user);
    }

    public void validateChangePasswordForm(Long userId, ChangePasswordForm form, BindingResult bindingResult) {
        User user = getUser(userId);

        if (StringUtils.hasText(form.getCurrentPassword())
                && !passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            bindingResult.rejectValue("currentPassword", "invalid", "Mật khẩu hiện tại không đúng");
        }

        if (StringUtils.hasText(form.getNewPassword())
                && StringUtils.hasText(form.getConfirmPassword())
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

    private Gender parseGender(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return Gender.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    private void validateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Vui lòng chọn ảnh đại diện");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new InvalidOperationException("Ảnh đại diện phải là tệp hình ảnh");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new InvalidOperationException("Ảnh đại diện không được vượt quá 5 MB");
        }
    }
}
