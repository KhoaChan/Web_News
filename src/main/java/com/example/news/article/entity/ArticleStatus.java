package com.example.news.article.entity;

public enum ArticleStatus {
    DRAFT,
    IN_REVIEW,
    CHANGES_REQUESTED,
    PUBLISHED,
    CANCELLED;

    public String getDisplayName() {
        return switch (this) {
            case DRAFT -> "Bản nháp";
            case IN_REVIEW -> "Chờ duyệt";
            case CHANGES_REQUESTED -> "Cần chỉnh sửa";
            case PUBLISHED -> "Đã xuất bản";
            case CANCELLED -> "Đã hủy";
        };
    }
}
