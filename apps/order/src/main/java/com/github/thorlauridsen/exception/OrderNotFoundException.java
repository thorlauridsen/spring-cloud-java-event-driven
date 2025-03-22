package com.github.thorlauridsen.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an order is not found.
 * Extends {@link DomainException}.
 */
public class OrderNotFoundException extends DomainException {

    /**
     * Constructor for an order not found exception.
     * Sets the http status to {@link HttpStatus#NOT_FOUND}.
     *
     * @param message The message of the exception.
     */
    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
