package ru.shtamov.neural_cutting.dto.analysis;

import ru.shtamov.neural_cutting.domain.enums.ProblemCategory;
import ru.shtamov.neural_cutting.domain.enums.ProblemSeverity;

import java.util.UUID;

public record ProblemResponse(
        UUID id,
        ProblemCategory category,
        ProblemSeverity severity,
        String fragment,
        String description
) {
}
