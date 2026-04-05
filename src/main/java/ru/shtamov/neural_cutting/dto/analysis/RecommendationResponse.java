package ru.shtamov.neural_cutting.dto.analysis;

import ru.shtamov.neural_cutting.domain.enums.RecommendationPriority;

import java.util.UUID;

public record RecommendationResponse(
        UUID id,
        String action,
        String example,
        RecommendationPriority priority
) {
}
