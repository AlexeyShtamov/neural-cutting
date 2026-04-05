package ru.shtamov.neural_cutting.dto.resume;

import jakarta.validation.constraints.Size;
import ru.shtamov.neural_cutting.domain.enums.Language;

public record UpdateResumeRequest(
        @Size(max = 200) String title,
        Language language,
        @Size(max = 200) String targetRole
) {
}
