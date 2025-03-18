package com.github.thorlauridsen.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a customer is not found.
 * Extends {@link DomainException}.
 */
public class CustomerNotFoundException extends DomainException {

    /**
     * Constructor for a customer not found exception.
     * Sets the http status to {@link HttpStatus#NOT_FOUND}.
     *
     * @param message The message of the exception.
     */
    public CustomerNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
