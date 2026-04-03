package com.example.news.user.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Size(max = 150, message = "Full name must be at most 150 characters")
    private String fullName;
}
