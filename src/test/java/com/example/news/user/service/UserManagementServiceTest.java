package com.example.news.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.AdminUserForm;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    void createUserShouldPersistEncodedPassword() {
        AdminUserForm form = new AdminUserForm();
        form.setUsername("editor1");
        form.setEmail("editor1@example.com");
        form.setFullName("Editor One");
        form.setRole("USER");
        form.setEnabled(true);
        form.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userManagementService.createUser(form);

        assertThat(user.getUsername()).isEqualTo("editor1");
        assertThat(user.getEmail()).isEqualTo("editor1@example.com");
        assertThat(user.getFullName()).isEqualTo("Editor One");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void validateAdminUserFormShouldRejectSelfDisableAndSelfDemotion() {
        AdminUserForm form = new AdminUserForm();
        form.setId(5L);
        form.setUsername("admin");
        form.setEmail("admin@example.com");
        form.setRole("USER");
        form.setEnabled(false);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "userForm");

        userManagementService.validateAdminUserForm(5L, form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("enabled")).isTrue();
        assertThat(bindingResult.hasFieldErrors("role")).isTrue();
    }

    @Test
    void toggleEnabledShouldRejectSelfDisable() {
        User admin = new User();
        admin.setId(7L);
        admin.setEnabled(true);

        when(userRepository.findById(7L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userManagementService.toggleEnabled(7L, 7L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("không thể tự khóa");
    }
}
