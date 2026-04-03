package com.example.news.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserShouldCreateEnabledUserWithUserRole() {
        RegisterForm form = new RegisterForm();
        form.setUsername("reader1");
        form.setEmail("reader1@example.com");
        form.setFullName("Reader One");
        form.setPassword("123456");
        form.setConfirmPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.registerUser(form);

        assertThat(user.getUsername()).isEqualTo("reader1");
        assertThat(user.getEmail()).isEqualTo("reader1@example.com");
        assertThat(user.getFullName()).isEqualTo("Reader One");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void validateRegistrationFormShouldRejectDuplicateUsernameEmailAndPasswordMismatch() {
        RegisterForm form = new RegisterForm();
        form.setUsername("reader1");
        form.setEmail("reader1@example.com");
        form.setPassword("123456");
        form.setConfirmPassword("654321");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "registerForm");

        when(userRepository.existsByUsername("reader1")).thenReturn(true);
        when(userRepository.existsByEmail("reader1@example.com")).thenReturn(true);

        authService.validateRegistrationForm(form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("username")).isTrue();
        assertThat(bindingResult.hasFieldErrors("email")).isTrue();
        assertThat(bindingResult.hasFieldErrors("confirmPassword")).isTrue();
    }
}
