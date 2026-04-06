package ru.shtamov.neural_cutting.dto.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.shtamov.neural_cutting.domain.enums.RecommendationPriority;

import java.util.UUID;

@Schema(description = "Рекомендация по улучшению резюме")
public record RecommendationResponse(
        @Schema(description = "ID рекомендации")
        UUID id,

        @Schema(description = "Действие для улучшения",
                example = "Добавьте количественные показатели достижений")
        String action,

        @Schema(description = "Пример реализации",
                example = "Снизил время отклика API на 35%, обрабатываю 1000+ запросов в секунду")
        String example,

        @Schema(description = "Приоритет рекомендации",
                example = "HIGH",
                allowableValues = {"LOW", "MEDIUM", "HIGH"})
        RecommendationPriority priority
) {
}
