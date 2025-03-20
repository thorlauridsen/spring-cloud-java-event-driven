package com.github.thorlauridsen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.model.OrderCreate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object for creating a new order.
 *
 * @param product description of product.
 * @param amount  amount to be paid.
 */
@Schema(
        description = "Data transfer object for creating a new order",
        example = """
                {
                    "product": "Computer",
                    "amount": 199.0
                }
                """
)
public record OrderCreateDto(
        @JsonProperty("product") String product,
        @JsonProperty("amount") double amount
) {

    /**
     * Method to convert {@link OrderCreateDto} to {@link OrderCreate} model.
     *
     * @return {@link OrderCreate}.
     */
    public OrderCreate toModel() {
        return new OrderCreate(
                product,
                amount
        );
    }
}
