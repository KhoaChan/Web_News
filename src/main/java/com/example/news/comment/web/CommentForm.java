package com.example.news.comment.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotNull(message = "Bài viết không hợp lệ")
    private Long articleId;

    @NotBlank(message = "Vui lòng nhập tên")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng nhập bình luận")
    @Size(max = 1000, message = "Bình luận tối đa 1000 ký tự")
    private String content;
}
