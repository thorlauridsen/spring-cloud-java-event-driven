package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.enumeration.EventType;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import java.util.UUID;

/**
 * This event represents when a payment has been completed.
 */
public class PaymentCompletedEventDto extends BaseEventDto {

    private final UUID paymentId;
    private final UUID orderId;
    private final double amount;

    /**
     * Constructor for PaymentCompletedEvent.
     *
     * @param id        UUID of the event.
     * @param paymentId UUID of the payment.
     * @param orderId   UUID of the order.
     * @param amount    amount of the payment.
     */
    @JsonCreator
    public PaymentCompletedEventDto(
            @JsonProperty("id") UUID id,
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("amount") double amount
    ) {
        super(id, EventType.PAYMENT_COMPLETED);
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    /**
     * Method to convert {@link PaymentCompletedEventDto} to {@link PaymentCompletedEvent} model.
     *
     * @return {@link PaymentCompletedEvent}.
     */
    public PaymentCompletedEvent toModel() {
        return new PaymentCompletedEvent(
                this.getId(),
                this.getPaymentId(),
                this.getOrderId(),
                this.getAmount()
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

    /**
     * Get the amount of the payment.
     * This getter is used by Jackson to serialize the event.
     *
     * @return amount of the payment.
     */
    public double getAmount() {
        return amount;
    }
}
