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

    @NotBlank(message = "Vui lòng nhập tiêu đề")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Vui lòng nhập slug")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch nối")
    private String slug;

    @NotBlank(message = "Vui lòng nhập tóm tắt")
    @Size(max = 500, message = "Tóm tắt tối đa 500 ký tự")
    private String summary;

    @NotBlank(message = "Vui lòng nhập nội dung")
    private String content;

    @NotNull(message = "Vui lòng chọn chuyên mục")
    private Long categoryId;

    @NotBlank(message = "Vui lòng chọn trạng thái")
    @Pattern(regexp = "^(DRAFT|IN_REVIEW|CHANGES_REQUESTED|PUBLISHED|CANCELLED)$", message = "Trạng thái không hợp lệ")
    private String status = "DRAFT";

    @Size(max = 1000, message = "Ghi chú duyệt tối đa 1000 ký tự")
    private String reviewNote;

    private String thumbnailUrl;
}
