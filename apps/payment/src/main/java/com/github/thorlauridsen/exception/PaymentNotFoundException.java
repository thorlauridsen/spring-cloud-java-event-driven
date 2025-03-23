package com.github.thorlauridsen.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a payment is not found.
 * Extends {@link DomainException}.
 */
public class PaymentNotFoundException extends DomainException {

    /**
     * Constructor for a payment not found exception.
     * Sets the http status to {@link HttpStatus#NOT_FOUND}.
     *
     * @param message The message of the exception.
     */
    public PaymentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
