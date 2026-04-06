package ru.shtamov.neural_cutting.mapper;

import org.springframework.stereotype.Component;
import ru.shtamov.neural_cutting.domain.AnalysisJob;
import ru.shtamov.neural_cutting.domain.AnalysisResult;
import ru.shtamov.neural_cutting.domain.Problem;
import ru.shtamov.neural_cutting.domain.Recommendation;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisHistoryItemResponse;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisJobResponse;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisResultResponse;
import ru.shtamov.neural_cutting.dto.analysis.ProblemResponse;
import ru.shtamov.neural_cutting.dto.analysis.RecommendationResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class AnalysisMapper {

    public AnalysisJobResponse toJobResponse(AnalysisJob job) {
        return new AnalysisJobResponse(
                job.getId(),
                job.getStatus(),
                job.getErrorCode(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getStartedAt(),
                job.getFinishedAt(),
                job.getResumeVersion().getId(),
                job.getVacancy().getId(),
                job.getAnalysisResult() != null ? job.getAnalysisResult().getId() : null
        );
    }

    public AnalysisResultResponse toResultResponse(AnalysisResult result) {
        return new AnalysisResultResponse(
                result.getId(),
                result.getAnalysisJob().getId(),
                result.getScore(),
                result.getGradeLabel(),
                result.getSummary(),
                result.getOverallFitPercent(),
                result.getCreatedAt(),
                result.getProblems().stream().map(this::toProblemResponse).toList(),
                result.getRecommendations().stream().map(this::toRecommendationResponse).toList(),
                result.getSkillMatchPercent(),
                parseCommaSeparated(result.getMatchedSkills()),
                parseCommaSeparated(result.getMissingSkills())
        );
    }

    public AnalysisHistoryItemResponse toHistoryItem(AnalysisJob job) {
        AnalysisResult result = job.getAnalysisResult();
        return new AnalysisHistoryItemResponse(
                job.getId(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getFinishedAt(),
                job.getVacancy().getId(),
                job.getVacancy().getTitle(),
                result != null ? result.getScore() : null,
                result != null ? result.getGradeLabel() : null,
                result != null ? result.getSummary() : null
        );
    }

    public List<ProblemResponse> toProblemResponses(List<Problem> problems) {
        return problems.stream().map(this::toProblemResponse).toList();
    }

    public List<RecommendationResponse> toRecommendationResponses(List<Recommendation> recommendations) {
        return recommendations.stream().map(this::toRecommendationResponse).toList();
    }

    private ProblemResponse toProblemResponse(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getCategory(),
                problem.getSeverity(),
                problem.getFragment(),
                problem.getDescription()
        );
    }

    private RecommendationResponse toRecommendationResponse(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getAction(),
                recommendation.getExample(),
                recommendation.getPriority()
        );
    }

    private List<String> parseCommaSeparated(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
