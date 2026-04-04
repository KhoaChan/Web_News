package com.example.news.category.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên chuyên mục")
    @Size(max = 100, message = "Tên chuyên mục tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng nhập slug")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch nối")
    private String slug;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;
}
