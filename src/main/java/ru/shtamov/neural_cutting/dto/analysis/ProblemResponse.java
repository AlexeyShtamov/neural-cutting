package ru.shtamov.neural_cutting.dto.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.shtamov.neural_cutting.domain.enums.ProblemCategory;
import ru.shtamov.neural_cutting.domain.enums.ProblemSeverity;

import java.util.UUID;

@Schema(description = "Выявленная проблема в резюме")
public record ProblemResponse(
        @Schema(description = "ID проблемы")
        UUID id,

        @Schema(description = "Категория проблемы",
                example = "SKILLS",
                allowableValues = {"ATS", "SKILLS", "IMPACT", "STRUCTURE", "LANGUAGE", "CONTENT"})
        ProblemCategory category,

        @Schema(description = "Серьёзность проблемы",
                example = "MEDIUM",
                allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
        ProblemSeverity severity,

        @Schema(description = "Фрагмент текста, связанный с проблемой")
        String fragment,

        @Schema(description = "Описание проблемы")
        String description
) {
}
