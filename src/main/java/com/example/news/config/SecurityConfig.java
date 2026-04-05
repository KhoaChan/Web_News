package com.example.news.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> handleSuccessRedirect(response, authentication);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/backoffice/media/**").hasAnyRole("ADMIN", "EDITOR", "AUTHOR")
                .requestMatchers(HttpMethod.POST, "/article/save/**").authenticated()
                .requestMatchers(
                        "/",
                        "/category/**",
                        "/article/**",
                        "/search",
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/error",
                        "/error/**")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/editor/**").hasAnyRole("EDITOR", "ADMIN")
                .requestMatchers("/author/**").hasAnyRole("AUTHOR", "ADMIN")
                .requestMatchers("/profile", "/profile/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    private void handleSuccessRedirect(jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            response.sendRedirect("/admin");
            return;
        }
        if (hasRole(authentication, "ROLE_EDITOR")) {
            response.sendRedirect("/editor");
            return;
        }
        if (hasRole(authentication, "ROLE_AUTHOR")) {
            response.sendRedirect("/author");
            return;
        }
        response.sendRedirect("/");
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
