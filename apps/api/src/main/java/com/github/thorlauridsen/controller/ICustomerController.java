package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.CustomerDto;
import com.github.thorlauridsen.dto.CustomerInputDto;
import com.github.thorlauridsen.dto.ErrorDto;
import com.github.thorlauridsen.exception.CustomerNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static com.github.thorlauridsen.controller.Endpoint.CUSTOMER_BASE_ENDPOINT;

/**
 * Customer controller interface.
 * This interface defines the endpoints for the customer controller.
 * It also defines the operations which will be used in the OpenAPI documentation.
 * The purpose with this interface is to separate the controller definition from the implementation.
 */
@Tag(name = "Customer Controller", description = "API for managing customers")
@RequestMapping(CUSTOMER_BASE_ENDPOINT)
public interface ICustomerController {

    /**
     * Save a customer.
     *
     * @return {@link ResponseEntity} with {@link CustomerDto}.
     */
    @PostMapping
    @Operation(
            summary = "Save a customer",
            description = "Save a customer"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Customer successfully created"
    )
    ResponseEntity<CustomerDto> save(@Valid @RequestBody CustomerInputDto customer);

    /**
     * Get customer by id.
     * This method returns a customer given an id.
     *
     * @param id UUID of the customer to retrieve.
     * @return {@link ResponseEntity} with {@link CustomerDto}.
     * @throws CustomerNotFoundException if the customer is not found.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a customer",
            description = "Retrieve a customer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved customer"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Customer not found with given id",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    ResponseEntity<CustomerDto> get(
            @Parameter(description = "UUID of the customer to retrieve", required = true)
            @PathVariable UUID id
    ) throws CustomerNotFoundException;
}
