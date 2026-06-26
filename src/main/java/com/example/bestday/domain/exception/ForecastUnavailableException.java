package com.example.bestday.domain.exception;

/**
 * Raised when the remote forecast cannot be retrieved or is empty/malformed.
 */
public class ForecastUnavailableException extends RuntimeException {

    public ForecastUnavailableException(String message) {
        super(message);
    }

    public ForecastUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
