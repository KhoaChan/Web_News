package com.example.news.ai.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.news.ai.web.AiSearchResponse;
import com.example.news.ai.web.AiSearchResultItem;
import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiSearchService {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{L}\\p{N}']+");
    private static final Set<String> STOP_WORDS = Set.of(
            "ai", "bao", "bai", "cho", "co", "cua", "gi", "giup", "hay", "khong", "la", "nao", "nhung", "tim",
            "toi", "ve", "viet", "with", "the", "and", "for", "that", "what", "where", "when");

    private final ArticleQueryService articleQueryService;

    public AiSearchResponse search(String question) {
        String normalizedQuestion = normalize(question);
        List<String> keywords = extractKeywords(normalizedQuestion);
        List<ScoredArticle> rankedArticles = articleQueryService.findAiSearchCandidates().stream()
                .map(article -> new ScoredArticle(article, score(article, keywords)))
                .filter(item -> item.score() > 0)
                .sorted(Comparator
                        .comparingInt(ScoredArticle::score)
                        .reversed()
                        .thenComparing(item -> item.article().getViews(), Comparator.reverseOrder()))
                .limit(6)
                .toList();

        List<AiSearchResultItem> results = rankedArticles.stream()
                .map(item -> new AiSearchResultItem(
                        item.article().getId(),
                        item.article().getSlug(),
                        item.article().getTitle(),
                        item.article().getSummary(),
                        item.article().getCategory() != null ? item.article().getCategory().getName() : "",
                        item.score()))
                .toList();

        return new AiSearchResponse(
                question,
                normalizedQuestion,
                keywords,
                buildAnswer(question, keywords, results),
                results);
    }

    private String buildAnswer(String question, List<String> keywords, List<AiSearchResultItem> results) {
        if (results.isEmpty()) {
            return "Chua tim thay bai viet phu hop voi cau hoi \"" + normalize(question) + "\". Thu bo sung tu khoa cu the hon.";
        }

        String topKeywords = keywords.isEmpty() ? "chu de ban dang hoi" : String.join(", ", keywords.subList(0, Math.min(3, keywords.size())));
        return "Minh da tim thay " + results.size() + " bai viet phu hop voi nhom tu khoa " + topKeywords
                + ". Bai noi bat nhat la \"" + results.get(0).title() + "\".";
    }

    private List<String> extractKeywords(String question) {
        if (!StringUtils.hasText(question)) {
            return List.of();
        }

        Set<String> keywords = new LinkedHashSet<>();
        var matcher = TOKEN_PATTERN.matcher(question.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String token = matcher.group();
            if (token.length() < 3 || STOP_WORDS.contains(token)) {
                continue;
            }
            keywords.add(token);
        }
        return keywords.stream().limit(6).toList();
    }

    private int score(Article article, List<String> keywords) {
        if (keywords.isEmpty()) {
            return 0;
        }
        String title = normalize(article.getTitle());
        String summary = normalize(article.getSummary());
        String content = normalize(stripHtml(article.getContent()));
        String category = article.getCategory() != null ? normalize(article.getCategory().getName()) : "";

        return keywords.stream()
                .mapToInt(keyword -> {
                    int score = 0;
                    if (title.contains(keyword)) {
                        score += 5;
                    }
                    if (summary.contains(keyword)) {
                        score += 3;
                    }
                    if (content.contains(keyword)) {
                        score += 2;
                    }
                    if (category.contains(keyword)) {
                        score += 2;
                    }
                    return score;
                })
                .sum();
    }

    private String stripHtml(String value) {
        return Objects.toString(value, "").replaceAll("<[^>]+>", " ");
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return Arrays.stream(value.trim().toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(StringUtils::hasText)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    private record ScoredArticle(Article article, int score) {
    }
}
