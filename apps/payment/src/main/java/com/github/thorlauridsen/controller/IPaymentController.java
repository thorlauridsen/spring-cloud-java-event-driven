package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.PaymentDto;
import com.github.thorlauridsen.exception.ErrorDto;
import com.github.thorlauridsen.exception.PaymentNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.github.thorlauridsen.controller.BaseEndpoint.PAYMENT_BASE_ENDPOINT;

/**
 * Payment controller interface.
 * This interface defines the endpoints for the payment controller.
 * It also defines the operations which will be used in the OpenAPI documentation.
 * The purpose with this interface is to separate the controller definition from the implementation.
 */
@Tag(name = "Payment Controller", description = "API for managing payments")
@RequestMapping(PAYMENT_BASE_ENDPOINT)
public interface IPaymentController {

    /**
     * Get payment by id.
     * This method returns a payment given an order id.
     *
     * @param orderId UUID of the order related to the payment.
     * @return {@link ResponseEntity} with {@link PaymentDto}.
     * @throws PaymentNotFoundException if the payment is not found.
     */
    @GetMapping("/{orderId}")
    @Operation(
            summary = "Retrieve a payment",
            description = "Retrieve a payment"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved payment"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found with given order id",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    ResponseEntity<PaymentDto> getByOrderId(
            @Parameter(description = "UUID of the order related to the payment", required = true)
            @PathVariable UUID orderId
    ) throws PaymentNotFoundException;
}
