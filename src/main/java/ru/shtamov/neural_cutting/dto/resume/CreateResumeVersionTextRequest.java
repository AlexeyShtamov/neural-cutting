package ru.shtamov.neural_cutting.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResumeVersionTextRequest(
        @NotBlank @Size(max = 50000) String text
) {
}
