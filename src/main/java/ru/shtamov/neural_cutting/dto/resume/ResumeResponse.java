package ru.shtamov.neural_cutting.dto.resume;

import ru.shtamov.neural_cutting.domain.enums.Language;

import java.time.Instant;
import java.util.UUID;

public record ResumeResponse(
        UUID id,
        String title,
        Language language,
        String targetRole,
        Instant createdAt,
        Instant updatedAt
) {
}
