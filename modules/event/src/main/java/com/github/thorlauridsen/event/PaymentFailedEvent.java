package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when a payment has failed.
 */
public class PaymentFailedEvent extends BaseEvent {

    private final UUID paymentId;
    private final UUID orderId;

    /**
     * Constructor for PaymentFailedEvent.
     *
     * @param id        UUID of the event.
     * @param paymentId UUID of the payment.
     * @param orderId   UUID of the order.
     */
    @JsonCreator
    public PaymentFailedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId
    ) {
        super(id, EventType.PAYMENT_FAILED);
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    /**
     * Get the UUID of the payment.
     * This getter is used by Jackson to serialize the event.
     *
     * @return UUID of the payment.
     */
    public UUID getPaymentId() {
        return paymentId;
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
}
