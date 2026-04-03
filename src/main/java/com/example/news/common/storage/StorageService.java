package com.example.news.common.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.news.common.exception.StorageException;

@Service
public class StorageService {

    private final Path uploadRoot;

    public StorageService(@Value("${app.storage.upload-dir:src/main/resources/static/uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (!StringUtils.hasText(originalFilename) || originalFilename.contains("..")) {
            throw new StorageException("Invalid file name");
        }

        String filename = System.currentTimeMillis() + "_" + originalFilename.replace(' ', '_');
        Path destination = uploadRoot.resolve(filename).normalize();

        try {
            Files.createDirectories(uploadRoot);
            Files.write(destination, file.getBytes());
        } catch (IOException exception) {
            throw new StorageException("Could not store file", exception);
        }

        return "/uploads/" + filename;
    }
}
