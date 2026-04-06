package ru.shtamov.neural_cutting.dto.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на импорт вакансии из HH.ru")
public record ImportFromHhRuRequest(
        @Schema(description = "ID или URL вакансии на HH.ru",
                example = "https://hh.ru/vacancy/92345678")
        @NotBlank String vacancyIdOrUrl
) {
}
