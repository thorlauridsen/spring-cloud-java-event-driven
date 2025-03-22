package com.github.thorlauridsen.exception;

import com.github.thorlauridsen.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

/**
 * Controller advisor for handling exceptions.
 * This ensures that whenever an exception is thrown, a proper error response is returned to the client.
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handles all domain exceptions.
     * If any {@link DomainException} is thrown, this method will
     * catch it and return a response entity with an {@link ErrorDto}.
     * The returned HTTP status code will be derived from the specific {@link DomainException}.
     *
     * @param exception The domain exception to handle.
     * @return A response entity with an {@link ErrorDto}.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorDto> handleDomainException(DomainException exception) {
        return error(exception, exception.getHttpStatus());
    }

    /**
     * Handles all exceptions.
     * If any exception is thrown, this method will catch it and return a response entity with an {@link ErrorDto}.
     * Returns an HTTP 500 status code if no domain exception is thrown.
     *
     * @param exception The exception to handle.
     * @return A response entity with an {@link ErrorDto}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleEverything(Exception exception) {
        return error(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Log exception and return a response entity with an {@link ErrorDto}.
     *
     * @param exception  {@link Exception}.
     * @param httpStatus {@link HttpStatus}.
     */
    private ResponseEntity<ErrorDto> error(
            Exception exception,
            HttpStatus httpStatus
    ) {
        var message = exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred";
        var errorDto = new ErrorDto(message, OffsetDateTime.now());

        logger.error(exception.getMessage(), exception);
        return ResponseEntity.status(httpStatus).body(errorDto);
    }
}
