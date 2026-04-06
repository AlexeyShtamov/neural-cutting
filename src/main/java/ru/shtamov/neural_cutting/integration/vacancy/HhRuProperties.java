package ru.shtamov.neural_cutting.integration.vacancy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.hh-ru")
public record HhRuProperties(
        String baseUrl,
        String userAgent,
        Duration connectTimeout,
        Duration readTimeout,
        boolean enabled
) {

    public static final String DEFAULT_BASE_URL = "https://api.hh.ru";
    public static final String DEFAULT_USER_AGENT = "NeuralCutting/1.0 (career-analysis-service)";

    public String baseUrl() {
        return baseUrl != null && !baseUrl.isBlank() ? baseUrl : DEFAULT_BASE_URL;
    }

    public String userAgent() {
        return userAgent != null && !userAgent.isBlank() ? userAgent : DEFAULT_USER_AGENT;
    }

    public Duration connectTimeout() {
        return connectTimeout != null ? connectTimeout : Duration.ofSeconds(5);
    }

    public Duration readTimeout() {
        return readTimeout != null ? readTimeout : Duration.ofSeconds(10);
    }
}
