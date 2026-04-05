package ru.shtamov.neural_cutting.dto.analysis;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AnalysisResultResponse(
        UUID id,
        UUID jobId,
        Integer score,
        String gradeLabel,
        String summary,
        Integer overallFitPercent,
        Instant createdAt,
        List<ProblemResponse> problems,
        List<RecommendationResponse> recommendations
) {
}
