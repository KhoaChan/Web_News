package com.example.news.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.ResourceNotFoundException;
import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.AdminUserForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    public AdminUserForm buildCreateForm() {
        AdminUserForm form = new AdminUserForm();
        form.setEnabled(true);
        form.setRole(Role.USER.name());
        return form;
    }

    public AdminUserForm getAdminUserForm(Long id) {
        User user = getUser(id);
        AdminUserForm form = new AdminUserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setFullName(user.getFullName());
        form.setRole(user.getRole().name());
        form.setEnabled(user.isEnabled());
        return form;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }

    public void validateAdminUserForm(Long currentAdminId, AdminUserForm form, BindingResult bindingResult) {
        String username = form.getUsername() == null ? "" : form.getUsername().trim();
        String email = form.getEmail() == null ? "" : form.getEmail().trim();

        if (form.getId() == null) {
            if (userRepository.existsByUsername(username)) {
                bindingResult.rejectValue("username", "duplicate", "Tên đăng nhập đã tồn tại");
            }
            if (userRepository.existsByEmail(email)) {
                bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
            }
            if (!StringUtils.hasText(form.getPassword())) {
                bindingResult.rejectValue("password", "required", "Vui lòng nhập mật khẩu cho người dùng mới");
            }
        } else {
            if (userRepository.existsByUsernameAndIdNot(username, form.getId())) {
                bindingResult.rejectValue("username", "duplicate", "Tên đăng nhập đã tồn tại");
            }
            if (userRepository.existsByEmailAndIdNot(email, form.getId())) {
                bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
            }
        }

        if (StringUtils.hasText(form.getPassword()) && form.getPassword().trim().length() < 6) {
            bindingResult.rejectValue("password", "length", "Mật khẩu phải có ít nhất 6 ký tự");
        }

        if (form.getId() != null && form.getId().equals(currentAdminId) && !form.isEnabled()) {
            bindingResult.rejectValue("enabled", "selfDisable", "Bạn không thể tự khóa tài khoản của chính mình");
        }

        if (form.getId() != null && form.getId().equals(currentAdminId) && !Role.ADMIN.name().equals(form.getRole())) {
            bindingResult.rejectValue("role", "selfDemote", "Bạn không thể tự đổi vai trò của mình khỏi Quản trị viên");
        }
    }

    public User createUser(AdminUserForm form) {
        User user = new User();
        mapFormToUser(user, form, true);
        return userRepository.save(user);
    }

    public User updateUser(Long currentAdminId, AdminUserForm form) {
        User user = getUser(form.getId());

        if (user.getId().equals(currentAdminId) && !Role.ADMIN.name().equals(form.getRole())) {
            throw new InvalidOperationException("Bạn không thể tự đổi vai trò của mình khỏi Quản trị viên");
        }

        if (user.getId().equals(currentAdminId) && !form.isEnabled()) {
            throw new InvalidOperationException("Bạn không thể tự khóa tài khoản của chính mình");
        }

        mapFormToUser(user, form, false);
        return userRepository.save(user);
    }

    public User toggleEnabled(Long currentAdminId, Long targetUserId) {
        User user = getUser(targetUserId);
        if (user.getId().equals(currentAdminId)) {
            throw new InvalidOperationException("Bạn không thể tự khóa tài khoản của chính mình");
        }
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    private void mapFormToUser(User user, AdminUserForm form, boolean creating) {
        user.setUsername(form.getUsername().trim());
        user.setEmail(form.getEmail().trim());
        user.setFullName(normalize(form.getFullName()));
        user.setRole(Role.valueOf(form.getRole()));
        user.setEnabled(form.isEnabled());

        if (creating || StringUtils.hasText(form.getPassword())) {
            user.setPassword(passwordEncoder.encode(form.getPassword().trim()));
        }
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
