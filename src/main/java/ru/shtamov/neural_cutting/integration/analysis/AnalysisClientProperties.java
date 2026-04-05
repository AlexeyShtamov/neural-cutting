package ru.shtamov.neural_cutting.integration.analysis;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.analysis")
public record AnalysisClientProperties(
        @NotBlank String mode,
        @NotBlank String baseUrl,
        @NotBlank String endpoint,
        Duration connectTimeout,
        Duration readTimeout
) {
}
