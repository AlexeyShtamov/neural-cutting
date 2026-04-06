package ru.shtamov.neural_cutting.dto.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на поиск вакансий на HH.ru")
public record SearchVacancyRequest(
        @Schema(description = "Поисковый запрос", example = "Java разработчик")
        @NotBlank @Size(max = 500) String query,

        @Schema(description = "ID региона (1 — Москва, 2 — Санкт-Петербург, 113 — Россия)", example = "1")
        @Size(max = 20) String areaId,

        @Schema(description = "Максимальное количество результатов (1-100)", example = "20")
        @Min(1) @Max(100) int limit
) {

    public SearchVacancyRequest {
        if (limit <= 0) {
            limit = 20;
        }
    }
}
