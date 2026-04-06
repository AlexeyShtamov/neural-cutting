package ru.shtamov.neural_cutting.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на авторизацию")
public record LoginRequest(
        @Schema(description = "Email адрес", example = "ivan.petrov@example.com")
        @NotBlank @Email @Size(max = 190) String email,

        @Schema(description = "Пароль", example = "SecurePass123!")
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
