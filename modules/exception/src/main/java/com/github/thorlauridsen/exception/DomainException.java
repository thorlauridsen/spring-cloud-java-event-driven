package com.github.thorlauridsen.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Abstract class representing a domain exception.
 * Custom domain exceptions should extend this class.
 */
@Getter
@RequiredArgsConstructor
public abstract class DomainException extends Exception {

    private final String message;
    private final HttpStatus httpStatus;
}
