package com.example.news.user.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserForm {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Tên đăng nhập chỉ được chứa chữ, số, dấu chấm, gạch dưới và gạch nối")
    private String username;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(max = 150, message = "Họ và tên tối đa 150 ký tự")
    private String fullName;

    @NotBlank(message = "Vui lòng chọn vai trò")
    @Pattern(regexp = "^(ADMIN|EDITOR|AUTHOR|USER)$", message = "Vai trò không hợp lệ")
    private String role = "USER";

    private boolean enabled = true;

    private String password;
}
