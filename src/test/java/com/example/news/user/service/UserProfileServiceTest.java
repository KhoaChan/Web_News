package com.example.news.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;

import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.web.ChangePasswordForm;
import com.example.news.user.web.UserProfileForm;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void validateProfileFormShouldRejectDuplicateEmail() {
        UserProfileForm form = new UserProfileForm();
        form.setEmail("reader@example.com");
        form.setFullName("Reader");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "profileForm");

        when(userRepository.existsByEmailAndIdNot("reader@example.com", 3L)).thenReturn(true);

        userProfileService.validateProfileForm(3L, form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("email")).isTrue();
    }

    @Test
    void updateProfileShouldTrimAndPersistFields() {
        User user = new User();
        user.setId(9L);
        user.setUsername("reader");
        user.setEmail("old@example.com");

        UserProfileForm form = new UserProfileForm();
        form.setEmail(" new@example.com ");
        form.setFullName(" Reader Name ");

        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userProfileService.updateProfile(9L, form);

        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedUser.getFullName()).isEqualTo("Reader Name");
    }

    @Test
    void validateChangePasswordFormShouldRejectIncorrectCurrentPasswordAndMismatch() {
        User user = new User();
        user.setId(10L);
        user.setPassword("encoded-old-password");

        ChangePasswordForm form = new ChangePasswordForm();
        form.setCurrentPassword("wrong-password");
        form.setNewPassword("123456");
        form.setConfirmPassword("654321");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "changePasswordForm");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-old-password")).thenReturn(false);

        userProfileService.validateChangePasswordForm(10L, form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("currentPassword")).isTrue();
        assertThat(bindingResult.hasFieldErrors("confirmPassword")).isTrue();
    }

    @Test
    void changePasswordShouldEncodeAndPersistPassword() {
        User user = new User();
        user.setId(11L);
        user.setPassword("encoded-old-password");

        ChangePasswordForm form = new ChangePasswordForm();
        form.setCurrentPassword("123456");
        form.setNewPassword("654321");
        form.setConfirmPassword("654321");

        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("654321")).thenReturn("encoded-new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userProfileService.changePassword(11L, form);

        assertThat(updatedUser.getPassword()).isEqualTo("encoded-new-password");
    }
}
