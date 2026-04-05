package ru.shtamov.neural_cutting.dto.analysis;

import ru.shtamov.neural_cutting.domain.enums.AnalysisJobStatus;

import java.time.Instant;
import java.util.UUID;

public record AnalysisJobResponse(
        UUID id,
        AnalysisJobStatus status,
        Integer errorCode,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt,
        Instant startedAt,
        Instant finishedAt,
        UUID resumeVersionId,
        UUID vacancyId,
        UUID resultId
) {
}
