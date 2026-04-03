package com.example.news.article.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleForm {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must use lowercase letters, numbers and hyphens")
    private String slug;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary must be at most 500 characters")
    private String summary;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(DRAFT|PUBLISHED)$", message = "Status must be DRAFT or PUBLISHED")
    private String status = "PUBLISHED";

    private String thumbnailUrl;
}
