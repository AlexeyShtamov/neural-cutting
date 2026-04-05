package ru.shtamov.neural_cutting.dto.analysis;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAnalysisJobRequest(
        @NotNull UUID resumeVersionId,
        @NotNull UUID vacancyId
) {
}
