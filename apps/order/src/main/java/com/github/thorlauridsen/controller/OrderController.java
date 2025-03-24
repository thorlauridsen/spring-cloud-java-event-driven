package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import com.github.thorlauridsen.exception.OrderNotFoundException;
import com.github.thorlauridsen.service.OrderService;
import java.util.UUID;
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
     * @param dto Input DTO for creating an order.
     * @return {@link ResponseEntity} with {@link OrderDto}.
     */
    @Override
    public ResponseEntity<OrderDto> create(OrderCreateDto dto) {
        var order = orderService.create(dto.toModel());
        return ResponseEntity.ok(OrderDto.fromModel(order));
    }

    /**
     * Get order given an id.
     * This method will convert the model to a DTO and return it.
     *
     * @param id UUID of the order to retrieve.
     * @return {@link ResponseEntity} with {@link OrderDto}.
     * @throws OrderNotFoundException if the order is not found.
     */
    @Override
    public ResponseEntity<OrderDto> get(UUID id) throws OrderNotFoundException {
        var order = orderService.findById(id);
        return ResponseEntity.ok(OrderDto.fromModel(order));
    }
}
