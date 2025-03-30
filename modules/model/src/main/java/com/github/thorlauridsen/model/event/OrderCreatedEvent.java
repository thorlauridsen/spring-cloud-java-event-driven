package com.github.thorlauridsen.model.event;

import com.github.thorlauridsen.model.enumeration.EventType;
import java.util.UUID;

/**
 * This event represents when an order has been created.
 */
public final class OrderCreatedEvent extends BaseEvent {

    private final UUID orderId;
    private final String product;
    private final double amount;

    /**
     * Constructor for OrderCreatedEvent.
     *
     * @param id      UUID of the event.
     * @param orderId UUID of the order.
     * @param product name of the product.
     * @param amount  amount of the product.
     */
    public OrderCreatedEvent(
            UUID id,
            UUID orderId,
            String product,
            double amount
    ) {
        super(id, EventType.ORDER_CREATED);
        this.orderId = orderId;
        this.product = product;
        this.amount = amount;
    }

    /**
     * Get the UUID of the order.
     * This getter is used by Jackson to serialize the event.
     *
     * @return UUID of the order.
     */
    public UUID getOrderId() {
        return orderId;
    }

    /**
     * Get the name of the product.
     * This getter is used by Jackson to serialize the event.
     *
     * @return name of the product.
     */
    public String getProduct() {
        return product;
    }

    /**
     * Get the amount of the product.
     * This getter is used by Jackson to serialize the event.
     *
     * @return amount of the product.
     */
    public double getAmount() {
        return amount;
    }
}
