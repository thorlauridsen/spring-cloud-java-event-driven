package com.github.thorlauridsen.exception;

import org.springframework.http.HttpStatus;

/**
 * Abstract class representing a domain exception.
 * Custom domain exceptions should extend this class.
 */
public abstract class DomainException extends Exception {

    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor for a domain exception.
     *
     * @param message    The message of the exception.
     * @param httpStatus The {@link HttpStatus} of the exception.
     */
    public DomainException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * Get the message of the exception.
     *
     * @return The message of the exception.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the {@link HttpStatus} of the exception.
     *
     * @return The {@link HttpStatus} of the exception.
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
