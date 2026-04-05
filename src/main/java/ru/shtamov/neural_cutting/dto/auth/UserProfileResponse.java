package ru.shtamov.neural_cutting.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        Instant createdAt
) {
}
