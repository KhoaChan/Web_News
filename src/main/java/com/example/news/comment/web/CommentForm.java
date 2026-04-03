package com.example.news.comment.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotNull(message = "Article is required")
    private Long articleId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String content;
}
