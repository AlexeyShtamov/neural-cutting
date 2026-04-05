package ru.shtamov.neural_cutting.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.shtamov.neural_cutting.domain.enums.Language;

public record CreateResumeRequest(
        @NotBlank @Size(max = 200) String title,
        @NotNull Language language,
        @NotBlank @Size(max = 200) String targetRole
) {
}
