package ru.shtamov.neural_cutting.dto.vacancy;

import java.time.Instant;
import java.util.UUID;

public record VacancyResponse(
        UUID id,
        String title,
        String company,
        String url,
        String text,
        Instant createdAt,
        Instant updatedAt
) {
}
