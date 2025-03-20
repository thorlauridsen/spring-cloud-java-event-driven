package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Order controller interface.
 * This interface defines the endpoints for the order controller.
 * It also defines the operations which will be used in the OpenAPI documentation.
 * The purpose with this interface is to separate the controller definition from the implementation.
 */
@Tag(name = "Order Controller", description = "API for managing orders")
@RequestMapping("/order")
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
}
