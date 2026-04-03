package com.example.news.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.findByUsername("reader").orElseGet(() -> {
            User user = new User();
            user.setUsername("reader");
            user.setEmail("reader@example.com");
            user.setFullName("Reader Account");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(Role.USER);
            user.setEnabled(true);
            return userRepository.save(user);
        });
    }

    @Test
    void loginPageShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerPageShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUserShouldBeRedirectedFromAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void anonymousUserShouldBeRedirectedFromProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void nonAdminUserShouldBeForbiddenFromAdminUsers() throws Exception {
        NewsUserPrincipal readerPrincipal = new NewsUserPrincipal(
                2L,
                "reader",
                "encoded-password",
                "Reader Account",
                "reader@example.com",
                Role.USER,
                true);

        mockMvc.perform(get("/admin/users").with(user(readerPrincipal)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserShouldAccessAdminUsers() throws Exception {
        NewsUserPrincipal adminPrincipal = new NewsUserPrincipal(
                1L,
                "admin",
                "encoded-password",
                "System Administrator",
                "admin@example.com",
                Role.ADMIN,
                true);

        mockMvc.perform(get("/admin/users").with(user(adminPrincipal)))
                .andExpect(status().isOk());
    }

    @Test
    void adminLoginShouldRedirectToAdminDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void userLoginShouldRedirectToHome() throws Exception {
        mockMvc.perform(formLogin("/login").user("reader").password("123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
