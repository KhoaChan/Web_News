package com.example.news.common.storage;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.news.common.exception.StorageException;

@Service
public class StorageService {

    private final Cloudinary cloudinary;

    public StorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (!StringUtils.hasText(originalFilename) || originalFilename.contains("..")) {
            throw new StorageException("Invalid file name");
        }

        try {
            @SuppressWarnings("rawtypes")
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("public_id", buildPublicId(originalFilename)));
            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new StorageException("Cloudinary did not return a file URL");
            }
            return secureUrl.toString();
        } catch (IOException exception) {
            throw new StorageException("Could not upload file to Cloudinary", exception);
        }
    }

    private String buildPublicId(String originalFilename) {
        String sanitizedName = originalFilename.replace(' ', '_');
        return System.currentTimeMillis() + "_" + sanitizedName;
    }
}
