package ru.shtamov.neural_cutting.dto.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Запрос на создание задачи анализа")
public record CreateAnalysisJobRequest(
        @Schema(description = "ID версии резюме", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID resumeVersionId,

        @Schema(description = "ID вакансии", example = "660e8400-e29b-41d4-a716-446655440001")
        @NotNull UUID vacancyId
) {
}
