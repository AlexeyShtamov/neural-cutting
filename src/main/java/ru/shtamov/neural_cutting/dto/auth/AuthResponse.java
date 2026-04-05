package ru.shtamov.neural_cutting.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UserProfileResponse user
) {
}
