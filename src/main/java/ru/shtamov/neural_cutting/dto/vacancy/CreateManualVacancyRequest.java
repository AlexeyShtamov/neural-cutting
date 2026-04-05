package ru.shtamov.neural_cutting.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateManualVacancyRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 200) String company,
        @Size(max = 500) String url,
        @NotBlank @Size(max = 50000) String text
) {
}
