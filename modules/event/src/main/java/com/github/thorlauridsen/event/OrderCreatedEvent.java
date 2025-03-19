package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when an order has been created.
 */
public class OrderCreatedEvent extends BaseEvent {

    private final String product;
    private final double amount;

    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("product") String product,
            @JsonProperty("amount") double amount
    ) {
        super(id, EventType.ORDER_CREATED);
        this.product = product;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "product='" + product + '\'' +
                ", amount=" + amount +
                '}';
    }
}
