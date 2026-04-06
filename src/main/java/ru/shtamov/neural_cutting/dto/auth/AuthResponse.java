package ru.shtamov.neural_cutting.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT токеном")
public record AuthResponse(
        @Schema(description = "JWT токен для авторизации", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Тип токена", example = "Bearer")
        String tokenType,

        @Schema(description = "Время жизни токена в секундах", example = "43200")
        long expiresInSeconds,

        @Schema(description = "Профиль пользователя")
        UserProfileResponse user
) {
}
