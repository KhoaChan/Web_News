package com.example.news.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Các đường dẫn ai cũng vào được (Khách vãng lai)
                .requestMatchers("/", "/category/**", "/article/**", "/search", "/api/search", "/register", "/css/**", "/images/**", "/error").permitAll()
                
                // Cấp 3: Admin
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                
                // Cấp 1, 2, 3: Phải đăng nhập mới được gửi bình luận
                .requestMatchers("/article/comment").authenticated()

                // Chỉ Author mới được vào trang Phóng viên
                .requestMatchers("/author/**").hasAuthority("ROLE_AUTHOR")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", false)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}