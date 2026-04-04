package com.example.news.comment.entity;

public enum CommentStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public String getDisplayName() {
        return switch (this) {
            case PENDING -> "Chờ duyệt";
            case APPROVED -> "Đã duyệt";
            case REJECTED -> "Từ chối";
        };
    }
}
