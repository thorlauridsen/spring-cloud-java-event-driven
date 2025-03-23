package com.github.thorlauridsen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data transfer object for an order.
 *
 * @param id      UUID of the order.
 * @param time    time order was created in database.
 * @param status  current order status.
 * @param product description of product.
 * @param amount  amount to be paid.
 */
@Schema(
        description = "Data transfer object for an order",
        example = """
                {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    "time": "2025-03-13T18:39:00Z",
                    "status": "CREATED",
                    "product": "Computer",
                    "amount": 199.0
                }
                """
)
public record OrderDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("time") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime time,
        @JsonProperty("status") OrderStatus status,
        @JsonProperty("product") String product,
        @JsonProperty("amount") double amount
) {

    /**
     * Convert this {@link OrderDto} to an {@link Order} model.
     *
     * @return {@link Order} model.
     */
    public Order toModel() {
        return new Order(
                id,
                time,
                status,
                product,
                amount
        );
    }

    /**
     * Static method to convert an {@link Order} model to an {@link OrderDto}.
     *
     * @param order {@link Order} to convert.
     * @return {@link OrderDto}.
     */
    public static OrderDto fromModel(Order order) {
        return new OrderDto(
                order.id(),
                order.time(),
                order.status(),
                order.product(),
                order.amount()
        );
    }
}
