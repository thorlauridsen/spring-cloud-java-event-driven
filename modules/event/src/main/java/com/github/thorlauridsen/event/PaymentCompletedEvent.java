package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when a payment has been completed.
 */
public class PaymentCompletedEvent extends BaseEvent {

    private final UUID orderId;
    private final double amount;

    @JsonCreator
    public PaymentCompletedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("amount") double amount
    ) {
        super(id, EventType.PAYMENT_COMPLETED);
        this.orderId = orderId;
        this.amount = amount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }
}
