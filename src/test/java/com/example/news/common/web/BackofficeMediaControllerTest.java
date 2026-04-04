package com.example.news.common.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.news.common.exception.StorageException;
import com.example.news.common.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class BackofficeMediaControllerTest {

    @Mock
    private StorageService storageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BackofficeMediaController(storageService)).build();
    }

    @Test
    void uploadImageShouldReturnUrlWhenFileIsValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "body.png", "image/png", "image".getBytes());
        when(storageService.store(file)).thenReturn("https://res.cloudinary.com/demo/image/upload/body.png");

        mockMvc.perform(multipart("/backoffice/media/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/demo/image/upload/body.png"));
    }

    @Test
    void uploadImageShouldRejectNonImageFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "body.pdf", "application/pdf", "file".getBytes());

        mockMvc.perform(multipart("/backoffice/media/image").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Chỉ chấp nhận tệp hình ảnh cho nội dung bài viết."));
    }

    @Test
    void uploadImageShouldRejectOversizedFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", new byte[5 * 1024 * 1024 + 1]);

        mockMvc.perform(multipart("/backoffice/media/image").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ảnh nội dung không được vượt quá 5 MB."));
    }

    @Test
    void uploadImageShouldReturnServerErrorWhenStorageFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "body.png", "image/png", "image".getBytes());
        when(storageService.store(file)).thenThrow(new StorageException("Không thể tải tệp lên Cloudinary"));

        mockMvc.perform(multipart("/backoffice/media/image").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Không thể tải tệp lên Cloudinary"));
    }
}
