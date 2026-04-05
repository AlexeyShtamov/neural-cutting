package ru.shtamov.neural_cutting.integration.analysis;

import ru.shtamov.neural_cutting.domain.enums.ProblemCategory;
import ru.shtamov.neural_cutting.domain.enums.ProblemSeverity;
import ru.shtamov.neural_cutting.domain.enums.RecommendationPriority;

import java.util.List;

public record ExternalAnalysisResponse(
        Integer score,
        String gradeLabel,
        String summary,
        Integer overallFitPercent,
        List<ExternalProblem> problems,
        List<ExternalRecommendation> recommendations
) {
    public record ExternalProblem(
            ProblemCategory category,
            ProblemSeverity severity,
            String fragment,
            String description
    ) {
    }

    public record ExternalRecommendation(
            String action,
            String example,
            RecommendationPriority priority
    ) {
    }
}
