package com.example.news.user.entity;

public enum Role {
    ADMIN,
    EDITOR,
    AUTHOR,
    USER;

    public String getDisplayName() {
        return switch (this) {
            case ADMIN -> "Quản trị viên";
            case EDITOR -> "Biên tập viên";
            case AUTHOR -> "Tác giả";
            case USER -> "Người dùng";
        };
    }
}
