package com.example.news.ai.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.news.ai.service.AiAssistantService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private AiAssistantService aiAssistantService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AiController(aiAssistantService)).build();
    }

    @Test
    void shouldReturnAiChatResponse() throws Exception {
        AiAssistantRequest request = new AiAssistantRequest(
                "/article/ai-news",
                "AI News",
                "ai-news",
                List.of(new AiAssistantMessage("user", "Tom tat bai nay")));

        when(aiAssistantService.chat(request)).thenReturn(new AiAssistantResponse(
                "Day la ban tom tat.",
                "Day la ban tom tat.",
                List.of("Tim bai lien quan", "Doc cho toi"),
                List.of(new AiAssistantArticleCard("ai-news-2", "AI News 2", "Tom tat ngan", "Lien quan den chu de AI")),
                true,
                "ok"));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Day la ban tom tat."))
                .andExpect(jsonPath("$.articles[0].slug").value("ai-news-2"))
                .andExpect(jsonPath("$.suggestions[0]").value("Tim bai lien quan"))
                .andExpect(jsonPath("$.configured").value(true));
    }
}
