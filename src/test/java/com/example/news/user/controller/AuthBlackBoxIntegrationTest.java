package com.example.news.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;
import com.example.news.user.security.NewsUserPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
class AuthBlackBoxIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User existingAdmin;
    private User readerUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        existingAdmin = createUser("admin", "admin@news.com", "System Administrator", Role.ADMIN);
        readerUser = createUser("reader", "reader@example.com", "Reader Account", Role.USER);
    }

    @Test
    void bbTc01RegisterWithValidDataShouldSucceed() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader01")
                        .param("email", "reader01@example.com")
                        .param("fullName", "Reader 01")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertThat(userRepository.findByUsername("reader01")).isPresent();
    }

    @Test
    void bbTc02RegisterShouldRejectUsernameWithInvalidCharacters() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader 01")
                        .param("email", "reader02@example.com")
                        .param("fullName", "Reader 02")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "username"));

        assertThat(userRepository.findByEmail("reader02@example.com")).isEmpty();
    }

    @Test
    void bbTc03RegisterShouldRejectDuplicateUsername() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", existingAdmin.getUsername())
                        .param("email", "reader03@example.com")
                        .param("fullName", "Reader 03")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "username"));

        assertThat(userRepository.findByEmail("reader03@example.com")).isEmpty();
    }

    @Test
    void bbTc04RegisterShouldRejectDuplicateEmail() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader04")
                        .param("email", existingAdmin.getEmail())
                        .param("fullName", "Reader 04")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "email"));

        assertThat(userRepository.findByUsername("reader04")).isEmpty();
    }

    @Test
    void bbTc05RegisterShouldRejectMismatchedConfirmationPassword() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader05")
                        .param("email", "reader05@example.com")
                        .param("fullName", "Reader 05")
                        .param("password", "123456")
                        .param("confirmPassword", "654321"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "confirmPassword"));

        assertThat(userRepository.findByUsername("reader05")).isEmpty();
    }

    @Test
    void bbTc06RegisterShouldRejectUsernameShorterThanMinimumLength() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "ab")
                        .param("email", "reader06@example.com")
                        .param("fullName", "Reader 06")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "username"));

        assertThat(userRepository.findByEmail("reader06@example.com")).isEmpty();
    }

    @Test
    void bbTc07RegisterShouldAcceptUsernameAtMinimumLength() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "abc")
                        .param("email", "reader07@example.com")
                        .param("fullName", "Reader 07")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertThat(userRepository.findByUsername("abc")).isPresent();
    }

    @Test
    void bbTc08RegisterShouldRejectPasswordShorterThanMinimumLength() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader08")
                        .param("email", "reader08@example.com")
                        .param("fullName", "Reader 08")
                        .param("password", "12345")
                        .param("confirmPassword", "12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "password"));

        assertThat(userRepository.findByUsername("reader08")).isEmpty();
    }

    @Test
    void bbTc09RegisterShouldAcceptPasswordAtMinimumLength() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "reader09")
                        .param("email", "reader09@example.com")
                        .param("fullName", "Reader 09")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertThat(userRepository.findByUsername("reader09")).isPresent();
    }

    @Test
    void bbTc10LoginWithValidCredentialsShouldSucceed() throws Exception {
        mockMvc.perform(formLogin("/login").user("reader").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void bbTc11LoginShouldRejectWrongPassword() throws Exception {
        mockMvc.perform(formLogin("/login").user("reader").password("saimatkhau"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void bbTc12LoginShouldRejectUnknownUsername() throws Exception {
        mockMvc.perform(formLogin("/login").user("khongtontai").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void bbTc13LoginShouldRejectEmptyCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void bbTc14LoginPageShouldRedirectAuthenticatedUserToDashboard() throws Exception {
        mockMvc.perform(get("/login").with(user(principal(readerUser))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    private User createUser(String username, String email, String fullName, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private NewsUserPrincipal principal(User user) {
        return new NewsUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                user.isEnabled());
    }
}
