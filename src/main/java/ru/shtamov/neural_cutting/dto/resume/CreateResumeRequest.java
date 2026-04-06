package ru.shtamov.neural_cutting.dto.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.shtamov.neural_cutting.domain.enums.Language;

@Schema(description = "Запрос на создание нового резюме")
public record CreateResumeRequest(
        @Schema(description = "Название резюме", example = "Резюме Java разработчика")
        @NotBlank @Size(max = 200) String title,

        @Schema(description = "Язык резюме", example = "RU")
        @NotNull Language language,

        @Schema(description = "Целевая должность", example = "Java Backend Developer")
        @NotBlank @Size(max = 200) String targetRole
) {
}
