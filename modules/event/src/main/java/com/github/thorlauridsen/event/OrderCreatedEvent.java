package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when an order has been created.
 */
public class OrderCreatedEvent extends BaseEvent {

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
    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("product") String product,
            @JsonProperty("amount") double amount
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
