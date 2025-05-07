package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.model.enumeration.EventType;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import java.util.UUID;
import lombok.Getter;

/**
 * This event represents when an order has been created.
 */
@Getter
public final class OrderCreatedEventDto extends BaseEventDto {

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
    public OrderCreatedEventDto(
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
     * Method to convert {@link OrderCreatedEventDto} to {@link OrderCreatedEvent} model.
     *
     * @return {@link OrderCreatedEvent}.
     */
    public OrderCreatedEvent toModel() {
        return new OrderCreatedEvent(
                this.getId(),
                this.getOrderId(),
                this.getProduct(),
                this.getAmount()
        );
    }
}
