package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when a payment has failed.
 */
public class PaymentFailedEvent extends BaseEvent {

    private final UUID orderId;

    @JsonCreator
    public PaymentFailedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("orderId") UUID orderId
    ) {
        super(id, EventType.PAYMENT_FAILED);
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
