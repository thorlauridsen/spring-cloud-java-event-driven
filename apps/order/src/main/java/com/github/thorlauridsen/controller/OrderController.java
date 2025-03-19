package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import com.github.thorlauridsen.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order controller class.
 * This class implements the {@link IOrderController} interface and
 * overrides the methods defined in the interface with implementations.
 * The controller is responsible for converting data transfer objects to models and vice versa.
 */
@RestController
public class OrderController implements IOrderController {

    private final OrderService orderService;

    /**
     * Constructor for OrderController.
     *
     * @param orderService {@link OrderService}.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Method to create a new order.
     *
     * @param dto Input DTO for creating a customer.
     * @return {@link ResponseEntity} with {@link OrderDto}.
     */
    @Override
    public ResponseEntity<OrderDto> create(OrderCreateDto dto) {
        var order = orderService.create(dto.toModel());
        return ResponseEntity.ok(OrderDto.fromModel(order));
    }
}
