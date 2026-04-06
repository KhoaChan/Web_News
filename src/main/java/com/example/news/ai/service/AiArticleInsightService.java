package com.example.news.ai.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.news.ai.web.AiArticleInsightsResponse;
import com.example.news.ai.web.AiRelatedArticleItem;
import com.example.news.article.entity.Article;
import com.example.news.article.service.ArticleQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiArticleInsightService {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("(?<=[.!?])\\s+");
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}']+");
    private static final int RELATED_LIMIT = 3;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "bai", "ban", "bi", "bo", "cho", "co", "cua", "cung", "da",
            "dang", "de", "den", "duoc", "giua", "hay", "khi", "khong", "la", "lam", "mot", "nam", "neu", "nhung",
            "nhieu", "noi", "o", "se", "sau", "tai", "the", "thi", "them", "theo", "this", "thoi", "tin", "to",
            "tren", "trong", "tu", "ve", "voi", "vua", "what", "where", "when", "why", "which");

    private final ArticleQueryService articleQueryService;

    public AiArticleInsightsResponse buildInsights(String slug) {
        Article article = articleQueryService.getPublishedArticleBySlug(slug);
        String cleanText = extractPlainText(article);
        List<String> sentences = splitSentences(cleanText);
        List<String> keywords = extractKeywords(article, cleanText);
        List<String> keyPoints = buildKeyPoints(article, sentences, keywords);
        List<String> suggestedQuestions = buildSuggestedQuestions(article, keywords);
        List<AiRelatedArticleItem> relatedArticles = articleQueryService.findRelatedPublishedArticles(article.getId(), keywords, RELATED_LIMIT)
                .stream()
                .map(this::toRelatedItem)
                .toList();

        int wordCount = countWords(cleanText);
        return new AiArticleInsightsResponse(
                article.getSlug(),
                article.getTitle(),
                cleanText,
                buildShortSummary(article, sentences),
                buildDetailedSummary(article, sentences),
                keyPoints,
                keywords,
                suggestedQuestions,
                estimateReadingTime(wordCount),
                wordCount,
                detectTone(article, cleanText),
                inferAudience(article, keywords),
                relatedArticles);
    }

    private AiRelatedArticleItem toRelatedItem(Article article) {
        return new AiRelatedArticleItem(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getSummary(),
                article.getCategory() != null ? article.getCategory().getName() : "");
    }

    private String extractPlainText(Article article) {
        String summary = normalizeWhitespace(article.getSummary());
        String content = normalizeWhitespace(HTML_TAG_PATTERN.matcher(Objects.toString(article.getContent(), "")).replaceAll(" "));
        return normalizeWhitespace((summary + ". " + content).trim());
    }

    private String buildShortSummary(Article article, List<String> sentences) {
        if (StringUtils.hasText(article.getSummary())) {
            return normalizeWhitespace(article.getSummary());
        }
        return joinSentences(sentences, 2);
    }

    private String buildDetailedSummary(Article article, List<String> sentences) {
        List<String> selectedSentences = new ArrayList<>();
        if (StringUtils.hasText(article.getSummary())) {
            selectedSentences.add(normalizeWhitespace(article.getSummary()));
        }
        selectedSentences.addAll(sentences.stream().limit(3).toList());
        return selectedSentences.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining(" "));
    }

    private List<String> buildKeyPoints(Article article, List<String> sentences, List<String> keywords) {
        List<String> points = new ArrayList<>();
        if (article.getCategory() != null && StringUtils.hasText(article.getCategory().getName())) {
            points.add("Chuyen muc: " + article.getCategory().getName() + ".");
        }
        if (!keywords.isEmpty()) {
            points.add("Chu de noi bat: " + String.join(", ", keywords.subList(0, Math.min(4, keywords.size()))) + ".");
        }
        points.addAll(sentences.stream().limit(3).map(this::trimSentence).toList());
        return points.stream().filter(StringUtils::hasText).distinct().limit(4).toList();
    }

    private List<String> buildSuggestedQuestions(Article article, List<String> keywords) {
        String title = normalizeWhitespace(article.getTitle());
        List<String> questions = new ArrayList<>();
        questions.add("Diem chinh quan trong nhat trong bai \"" + title + "\" la gi?");
        if (!keywords.isEmpty()) {
            questions.add("Bai viet lien quan den " + keywords.get(0) + " nao dang co tren trang?");
        }
        questions.add("Doc gia nen quan tam tac dong thuc te nao tu bai viet nay?");
        questions.add("Co the tom tat bai viet nay thanh 3 y gon khong?");
        return questions.stream().distinct().limit(4).toList();
    }

    private String detectTone(Article article, String cleanText) {
        String source = (Objects.toString(article.getTitle(), "") + " " + cleanText).toLowerCase(Locale.ROOT);
        if (source.contains("phan tich") || source.contains("du bao") || source.contains("so lieu")) {
            return "Phan tich";
        }
        if (source.contains("huong dan") || source.contains("cach") || source.contains("meo")) {
            return "Huong dan";
        }
        return "Thong tin";
    }

    private String inferAudience(Article article, List<String> keywords) {
        if (article.getCategory() != null && StringUtils.hasText(article.getCategory().getName())) {
            return "Doc gia quan tam den " + article.getCategory().getName().toLowerCase(Locale.ROOT) + ".";
        }
        if (!keywords.isEmpty()) {
            return "Doc gia dang tim hieu ve " + keywords.get(0) + ".";
        }
        return "Doc gia muon nam nhanh noi dung bai viet.";
    }

    private List<String> splitSentences(String cleanText) {
        if (!StringUtils.hasText(cleanText)) {
            return List.of();
        }
        return Arrays.stream(SENTENCE_SPLIT_PATTERN.split(cleanText))
                .map(this::trimSentence)
                .filter(StringUtils::hasText)
                .toList();
    }

    private String trimSentence(String sentence) {
        return normalizeWhitespace(sentence).replaceAll("^[\\-•]+\\s*", "");
    }

    private String joinSentences(List<String> sentences, int limit) {
        return sentences.stream().limit(limit).collect(Collectors.joining(" "));
    }

    private List<String> extractKeywords(Article article, String cleanText) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<String> sources = List.of(
                Objects.toString(article.getTitle(), ""),
                Objects.toString(article.getSummary(), ""),
                cleanText);

        for (String source : sources) {
            var matcher = WORD_PATTERN.matcher(source.toLowerCase(Locale.ROOT));
            while (matcher.find()) {
                String word = matcher.group();
                if (word.length() < 3 || STOP_WORDS.contains(word)) {
                    continue;
                }
                counts.merge(word, 1, Integer::sum);
            }
        }

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .limit(6)
                .toList();
    }

    private int countWords(String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }
        var matcher = WORD_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int estimateReadingTime(int wordCount) {
        return Math.max(1, (int) Math.ceil(wordCount / 220.0));
    }

    private String normalizeWhitespace(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        return MULTI_SPACE_PATTERN.matcher(input.replace("&nbsp;", " ").trim()).replaceAll(" ");
    }
}
