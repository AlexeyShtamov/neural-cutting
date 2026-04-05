package ru.shtamov.neural_cutting.integration.analysis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.shtamov.neural_cutting.domain.enums.ProblemCategory;
import ru.shtamov.neural_cutting.domain.enums.ProblemSeverity;
import ru.shtamov.neural_cutting.domain.enums.RecommendationPriority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(prefix = "app.analysis", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubAnalysisClient implements AnalysisClient {

    private static final Set<String> STOP_WORDS = Set.of(
            "and", "the", "for", "with", "that", "this", "have", "from", "your", "about", "into", "will",
            "java", "developer", "engineer", "backend", "spring", "boot", "service", "services", "team"
    );

    @Override
    public ExternalAnalysisResponse analyze(ExternalAnalysisRequest request) {
        String resumeText = normalizeText(
                StringUtils.hasText(request.resume().text())
                        ? request.resume().text()
                        : request.resume().resumeTitle() + " " + safe(request.resume().fileName())
        );
        String vacancyText = normalizeText(
                request.vacancy().title() + " " + safe(request.vacancy().company()) + " " + request.vacancy().text()
        );

        Set<String> resumeTokens = tokenize(resumeText);
        Set<String> vacancyTokens = tokenize(vacancyText);
        Set<String> overlap = new LinkedHashSet<>(resumeTokens);
        overlap.retainAll(vacancyTokens);

        List<ExternalAnalysisResponse.ExternalProblem> problems = new ArrayList<>();
        List<ExternalAnalysisResponse.ExternalRecommendation> recommendations = new ArrayList<>();

        int score = 58 + Math.min(overlap.size() * 4, 28);

        if (!StringUtils.hasText(request.resume().text())) {
            score -= 8;
            problems.add(new ExternalAnalysisResponse.ExternalProblem(
                    ProblemCategory.ATS,
                    ProblemSeverity.MEDIUM,
                    request.resume().fileName(),
                    "Version was uploaded as a file without extracted text. Analysis is based on metadata and may be less accurate."
            ));
            recommendations.add(new ExternalAnalysisResponse.ExternalRecommendation(
                    "Add a text-based version or connect document text extraction for higher-quality analysis.",
                    "Paste the resume content as text or enrich the pipeline with OCR/parser extraction.",
                    RecommendationPriority.MEDIUM
            ));
        }

        if (resumeText.length() < 400) {
            score -= 10;
            problems.add(new ExternalAnalysisResponse.ExternalProblem(
                    ProblemCategory.STRUCTURE,
                    ProblemSeverity.MEDIUM,
                    snippet(resumeText),
                    "Resume content is quite short for a targeted application and likely under-describes experience."
            ));
            recommendations.add(new ExternalAnalysisResponse.ExternalRecommendation(
                    "Expand the resume with stronger experience bullets and project outcomes.",
                    "Add 3-5 measurable bullets for the most relevant role, including impact and technologies.",
                    RecommendationPriority.HIGH
            ));
        }

        if (!resumeText.matches(".*\\d+.*")) {
            score -= 12;
            problems.add(new ExternalAnalysisResponse.ExternalProblem(
                    ProblemCategory.IMPACT,
                    ProblemSeverity.HIGH,
                    snippet(resumeText),
                    "Resume does not show quantified outcomes, which weakens credibility and business impact."
            ));
            recommendations.add(new ExternalAnalysisResponse.ExternalRecommendation(
                    "Quantify achievements with metrics, scale, or delivery speed.",
                    "Reduced API latency by 35%, migrated 12 services, or improved uptime to 99.95%.",
                    RecommendationPriority.HIGH
            ));
        }

        List<String> missingVacancyKeywords = vacancyTokens.stream()
                .filter(token -> !resumeTokens.contains(token))
                .limit(3)
                .toList();

        if (!missingVacancyKeywords.isEmpty()) {
            score -= missingVacancyKeywords.size() * 4;
            problems.add(new ExternalAnalysisResponse.ExternalProblem(
                    ProblemCategory.SKILLS,
                    ProblemSeverity.MEDIUM,
                    String.join(", ", missingVacancyKeywords),
                    "Several vacancy keywords are not reflected in the resume, which can lower keyword matching."
            ));
            recommendations.add(new ExternalAnalysisResponse.ExternalRecommendation(
                    "Tailor the resume to the vacancy vocabulary and stack requirements.",
                    "Mention the relevant technologies explicitly: " + String.join(", ", missingVacancyKeywords) + ".",
                    RecommendationPriority.MEDIUM
            ));
        }

        if (problems.isEmpty()) {
            recommendations.add(new ExternalAnalysisResponse.ExternalRecommendation(
                    "Keep the resume focused on the target role and preserve the current strong signal.",
                    "Retain concise bullets, direct stack alignment, and quantified impact in the latest version.",
                    RecommendationPriority.LOW
            ));
        }

        int boundedScore = Math.max(20, Math.min(score, 96));
        int overallFitPercent = Math.max(25, Math.min(boundedScore + overlap.size(), 98));
        String gradeLabel = gradeForScore(boundedScore);
        String summary = buildSummary(overlap, missingVacancyKeywords, boundedScore, request);

        return new ExternalAnalysisResponse(
                boundedScore,
                gradeLabel,
                summary,
                overallFitPercent,
                problems,
                recommendations
        );
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.split("[^\\p{L}\\p{Nd}]+"))
                .map(token -> token.toLowerCase(Locale.ROOT).trim())
                .filter(token -> token.length() >= 4)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeText(String text) {
        return safe(text).replaceAll("\\s+", " ").trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String snippet(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return text.length() <= 200 ? text : text.substring(0, 200) + "...";
    }

    private String gradeForScore(int score) {
        if (score >= 90) {
            return "EXCELLENT";
        }
        if (score >= 80) {
            return "GOOD";
        }
        if (score >= 65) {
            return "SOLID";
        }
        if (score >= 50) {
            return "NEEDS_IMPROVEMENT";
        }
        return "WEAK";
    }

    private String buildSummary(
            Set<String> overlap,
            List<String> missingVacancyKeywords,
            int score,
            ExternalAnalysisRequest request
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("Resume version shows ");
        if (score >= 80) {
            builder.append("strong alignment");
        } else if (score >= 65) {
            builder.append("moderate alignment");
        } else {
            builder.append("limited alignment");
        }
        builder.append(" with vacancy '").append(request.vacancy().title()).append("'.");

        if (!overlap.isEmpty()) {
            builder.append(" Matching signals include ").append(String.join(", ", overlap.stream().limit(5).toList())).append(".");
        }
        if (!missingVacancyKeywords.isEmpty()) {
            builder.append(" Strengthen coverage for ").append(String.join(", ", missingVacancyKeywords)).append(".");
        }
        return builder.toString();
    }
}
