package com.example.news.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.news.user.entity.Role;
import com.example.news.user.entity.User;
import com.example.news.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanReaderAccounts() {
        userRepository.findByUsername("newreader").ifPresent(userRepository::delete);
        userRepository.findByUsername("duplicate-reader").ifPresent(userRepository::delete);
    }

    @Test
    void registerShouldCreateEnabledUserWithUserRole() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newreader")
                        .param("email", "newreader@example.com")
                        .param("fullName", "New Reader")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        User savedUser = userRepository.findByUsername("newreader").orElseThrow();
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedUser.isEnabled()).isTrue();
        assertThat(savedUser.getFullName()).isEqualTo("New Reader");
        assertThat(passwordEncoder.matches("123456", savedUser.getPassword())).isTrue();
    }

    @Test
    void registerShouldReturnFormWhenPasswordConfirmationMismatches() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "duplicate-reader")
                        .param("email", "duplicate@example.com")
                        .param("fullName", "Duplicate Reader")
                        .param("password", "123456")
                        .param("confirmPassword", "654321"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "confirmPassword"));
    }
}
