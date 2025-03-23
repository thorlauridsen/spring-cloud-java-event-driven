package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.model.enumeration.EventType;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import java.util.UUID;

/**
 * This event represents when a payment has failed.
 */
public class PaymentFailedEventDto extends BaseEventDto {

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
    public PaymentFailedEventDto(
            @JsonProperty("id") UUID id,
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId
    ) {
        super(id, EventType.PAYMENT_FAILED);
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    /**
     * Method to convert {@link PaymentFailedEventDto} to {@link PaymentFailedEvent} model.
     *
     * @return {@link PaymentFailedEvent}.
     */
    public PaymentFailedEvent toModel() {
        return new PaymentFailedEvent(
                this.getId(),
                this.getPaymentId(),
                this.getOrderId()
        );
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
