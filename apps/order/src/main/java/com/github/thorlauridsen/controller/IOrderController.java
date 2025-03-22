package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.ErrorDto;
import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import com.github.thorlauridsen.exception.OrderNotFoundException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.github.thorlauridsen.controller.BaseEndpoint.ORDER_BASE_ENDPOINT;

/**
 * Order controller interface.
 * This interface defines the endpoints for the order controller.
 * It also defines the operations which will be used in the OpenAPI documentation.
 * The purpose with this interface is to separate the controller definition from the implementation.
 */
@Tag(name = "Order Controller", description = "API for managing orders")
@RequestMapping(ORDER_BASE_ENDPOINT)
public interface IOrderController {

    /**
     * Create an order.
     *
     * @return {@link ResponseEntity} with {@link OrderDto}.
     */
    @PostMapping("/create")
    @Operation(
            summary = "Save an order",
            description = "Save an order"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Order successfully created"
    )
    ResponseEntity<OrderDto> create(@RequestBody OrderCreateDto dto);

    /**
     * Get order by id.
     * This method returns an order given an id.
     *
     * @param id UUID of the order to retrieve.
     * @return {@link ResponseEntity} with {@link OrderDto}.
     * @throws OrderNotFoundException if the order is not found.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a order",
            description = "Retrieve a order"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved order"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found with given id",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    ResponseEntity<OrderDto> get(
            @Parameter(description = "UUID of the order to retrieve", required = true)
            @PathVariable UUID id
    ) throws OrderNotFoundException;
}
