package com.example.news.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.storage.StorageService;
import com.example.news.user.entity.Gender;
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

    @Mock
    private StorageService storageService;

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
    void validateProfileFormShouldRejectFutureBirthDate() {
        UserProfileForm form = new UserProfileForm();
        form.setEmail("reader@example.com");
        form.setBirthDate(LocalDate.now().plusDays(1));

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "profileForm");

        userProfileService.validateProfileForm(3L, form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("birthDate")).isTrue();
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
        form.setBirthDate(LocalDate.of(2000, 5, 10));
        form.setGender("female");

        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userProfileService.updateProfile(9L, form);

        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedUser.getFullName()).isEqualTo("Reader Name");
        assertThat(updatedUser.getBirthDate()).isEqualTo(LocalDate.of(2000, 5, 10));
        assertThat(updatedUser.getGender()).isEqualTo(Gender.FEMALE);
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

    @Test
    void updateAvatarShouldPersistStoredUrl() {
        User user = new User();
        user.setId(12L);
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.png", "image/png", new byte[] { 1, 2, 3 });

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(storageService.store(file)).thenReturn("https://cdn.example.com/avatar.png");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userProfileService.updateAvatar(12L, file);

        assertThat(updatedUser.getAvatarUrl()).isEqualTo("https://cdn.example.com/avatar.png");
    }

    @Test
    void updateAvatarShouldRejectInvalidFileType() {
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.txt", "text/plain", "abc".getBytes());

        assertThatThrownBy(() -> userProfileService.updateAvatar(12L, file))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("tệp hình ảnh");
    }
}
