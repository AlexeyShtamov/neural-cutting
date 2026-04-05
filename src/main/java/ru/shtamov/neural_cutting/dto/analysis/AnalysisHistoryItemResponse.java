package ru.shtamov.neural_cutting.dto.analysis;

import ru.shtamov.neural_cutting.domain.enums.AnalysisJobStatus;

import java.time.Instant;
import java.util.UUID;

public record AnalysisHistoryItemResponse(
        UUID id,
        AnalysisJobStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        UUID vacancyId,
        String vacancyTitle,
        Integer score,
        String gradeLabel,
        String summary
) {
}
