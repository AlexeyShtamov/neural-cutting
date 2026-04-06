package ru.shtamov.neural_cutting.dto.analysis;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Результат анализа резюме")
public record AnalysisResultResponse(
        @Schema(description = "ID результата")
        UUID id,

        @Schema(description = "ID задачи анализа")
        UUID jobId,

        @Schema(description = "Оценка резюме (20-96)", example = "78")
        Integer score,

        @Schema(description = "Оценка буквами", example = "GOOD",
                allowableValues = {"EXCELLENT", "GOOD", "SOLID", "NEEDS_IMPROVEMENT", "WEAK"})
        String gradeLabel,

        @Schema(description = "Краткое резюме анализа")
        String summary,

        @Schema(description = "Общий процент соответствия (25-98)", example = "82")
        Integer overallFitPercent,

        @Schema(description = "Дата и время создания")
        Instant createdAt,

        @Schema(description = "Список выявленных проблем")
        List<ProblemResponse> problems,

        @Schema(description = "Список рекомендаций по улучшению")
        List<RecommendationResponse> recommendations,

        @Schema(description = "Процент соответствия навыков (0-100)", example = "65")
        Integer skillMatchPercent,

        @Schema(description = "Навыки, которые есть и в резюме, и в вакансии",
                example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
        List<String> matchedSkills,

        @Schema(description = "Навыки из вакансии, которых нет в резюме",
                example = "[\"Kubernetes\", \"AWS\"]")
        List<String> missingSkills
) {
}
