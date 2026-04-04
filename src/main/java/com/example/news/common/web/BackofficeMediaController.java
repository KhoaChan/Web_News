package com.example.news.common.web;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.common.exception.InvalidOperationException;
import com.example.news.common.exception.StorageException;
import com.example.news.common.storage.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/backoffice/media")
@RequiredArgsConstructor
public class BackofficeMediaController {

    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;

    private final StorageService storageService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadArticleImage(@RequestParam("file") MultipartFile file) {
        try {
            validateImage(file);
            String url = storageService.store(file);
            if (url == null || url.isBlank()) {
                throw new StorageException("Không thể lấy đường dẫn ảnh sau khi tải lên.");
            }
            return ResponseEntity.ok(Map.of("url", url));
        } catch (InvalidOperationException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        } catch (StorageException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Vui lòng chọn một ảnh để tải lên.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new InvalidOperationException("Chỉ chấp nhận tệp hình ảnh cho nội dung bài viết.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new InvalidOperationException("Ảnh nội dung không được vượt quá 5 MB.");
        }
    }
}
