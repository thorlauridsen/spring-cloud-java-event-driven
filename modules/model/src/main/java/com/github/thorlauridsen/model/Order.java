package com.github.thorlauridsen.model;

import com.github.thorlauridsen.enumeration.OrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Record class representing the domain model "Order".
 *
 * @param id      UUID of the payment.
 * @param time    time order was created in database.
 * @param status  current order status.
 * @param product description of product.
 * @param amount  amount to be paid.
 */
public record Order(
        UUID id,
        OffsetDateTime time,
        OrderStatus status,
        String product,
        double amount
) {

    /**
     * Static method to update the status of an order.
     * This method is provided to increase immutability.
     *
     * @param order     {@link Order}
     * @param newStatus {@link OrderStatus}
     * @return {@link Order} with newly updated {@link OrderStatus}.
     */
    public static Order updateStatus(
            Order order,
            OrderStatus newStatus
    ) {
        return new Order(
                order.id(),
                order.time(),
                newStatus,
                order.product(),
                order.amount()
        );
    }
}
