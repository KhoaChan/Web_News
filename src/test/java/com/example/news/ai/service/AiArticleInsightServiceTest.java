package com.example.news.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.news.ai.web.AiArticleInsightsResponse;
import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.service.ArticleQueryService;
import com.example.news.category.entity.Category;

@ExtendWith(MockitoExtension.class)
class AiArticleInsightServiceTest {

    @Mock
    private ArticleQueryService articleQueryService;

    @InjectMocks
    private AiArticleInsightService aiArticleInsightService;

    @Test
    void buildInsightsShouldCreateCleanReadableResponseFromHtmlArticle() {
        Category category = new Category();
        category.setName("Cong nghe");

        Article article = new Article();
        article.setId(1L);
        article.setSlug("ai-moi");
        article.setTitle("AI moi tren thi truong");
        article.setSummary(" ");
        article.setContent("<p>AI dang thay doi cach doc tin.</p><ul><li>* Cong cu moi cho toa soan</li></ul>");
        article.setCategory(category);
        article.setStatus(ArticleStatus.PUBLISHED);

        Article related = new Article();
        related.setId(2L);
        related.setSlug("ai-lien-quan");
        related.setTitle("Tin lien quan");
        related.setSummary("Tom tat bai viet lien quan");
        related.setCategory(category);

        when(articleQueryService.getPublishedArticleBySlug("ai-moi")).thenReturn(article);
        when(articleQueryService.findRelatedPublishedArticles(eq(1L), anyList(), eq(3))).thenReturn(List.of(related));

        AiArticleInsightsResponse response = aiArticleInsightService.buildInsights("ai-moi");

        assertThat(response.articleSlug()).isEqualTo("ai-moi");
        assertThat(response.cleanText()).startsWith("AI dang thay doi cach doc tin.");
        assertThat(response.cleanText()).doesNotStartWith(".");
        assertThat(response.shortSummary()).isNotBlank();
        assertThat(response.detailedSummary()).isNotBlank();
        assertThat(response.keyPoints()).isNotEmpty();
        assertThat(response.keyPoints()).allMatch(point -> point != null && !point.isBlank());
        assertThat(response.keywords()).isNotEmpty();
        assertThat(response.readingTimeMinutes()).isEqualTo(1);
        assertThat(response.wordCount()).isGreaterThan(0);
        assertThat(response.relatedArticles()).hasSize(1);
        assertThat(response.relatedArticles().get(0).slug()).isEqualTo("ai-lien-quan");

        verify(articleQueryService).findRelatedPublishedArticles(eq(1L), anyList(), eq(3));
    }
}
