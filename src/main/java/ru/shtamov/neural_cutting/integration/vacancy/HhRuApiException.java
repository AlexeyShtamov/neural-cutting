package ru.shtamov.neural_cutting.integration.vacancy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when HH.ru API operations fail.
 */
public class HhRuApiException extends RuntimeException {

    private final int statusCode;

    public HhRuApiException(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_GATEWAY.value();
    }

    public HhRuApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = HttpStatus.BAD_GATEWAY.value();
    }

    public HhRuApiException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
