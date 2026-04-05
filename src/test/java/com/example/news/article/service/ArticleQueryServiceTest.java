package com.example.news.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.news.article.entity.Article;
import com.example.news.article.entity.ArticleStatus;
import com.example.news.article.repository.ArticleRepository;

@ExtendWith(MockitoExtension.class)
class ArticleQueryServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleQueryService articleQueryService;

    @Test
    void searchPublishedArticlesShouldFallbackToNewestWhenRelevantHasNoKeyword() {
        Article newestArticle = article("Newest", 4, now().minusHours(2));

        when(articleRepository.findPublishedBySearchFiltersOrderedByFreshness(eq(ArticleStatus.PUBLISHED), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(newestArticle)));

        Page<Article> result = articleQueryService.searchPublishedArticles("", "all", "all", "relevant", 0, 5);

        assertThat(result.getContent()).containsExactly(newestArticle);
    }

    @Test
    void searchPublishedArticlesShouldRankRelevantArticlesByWeightedKeywordMatches() {
        Article titleMatch = article("Đội tuyển quốc gia", 10, now().minusDays(1));
        titleMatch.setSummary("Thông tin bên lề");
        titleMatch.setContent("Không có gì thêm");

        Article summaryContentMatch = article("Bản tin thể thao", 20, now().minusHours(1));
        summaryContentMatch.setSummary("Đội hình mới được công bố");
        summaryContentMatch.setContent("Không có gì thêm");

        when(articleRepository.findPublishedCandidatesForSearch(eq(ArticleStatus.PUBLISHED), eq(null), eq(null)))
                .thenReturn(List.of(summaryContentMatch, titleMatch));

        Page<Article> result = articleQueryService.searchPublishedArticles("đội", "all", "all", "relevant", 0, 5);

        assertThat(result.getContent()).containsExactly(titleMatch, summaryContentMatch);
    }

    @Test
    void findMostViewedPublishedArticlesShouldReturnRepositoryResult() {
        Article first = article("A", 12, now());
        Article second = article("B", 8, now().minusDays(1));

        when(articleRepository.findMostViewedPublishedArticles(eq(ArticleStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(List.of(first, second));

        List<Article> result = articleQueryService.findMostViewedPublishedArticles();

        assertThat(result).containsExactly(first, second);
    }

    private Article article(String title, int views, LocalDateTime publishedAt) {
        Article article = new Article();
        article.setTitle(title);
        article.setViews(views);
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(publishedAt);
        article.setCreatedAt(publishedAt.minusHours(2));
        article.setSummary("");
        article.setContent("");
        return article;
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }
}
