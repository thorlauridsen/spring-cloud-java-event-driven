package com.github.thorlauridsen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.enumeration.PaymentStatus;
import com.github.thorlauridsen.model.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data transfer object for a payment.
 *
 * @param id      UUID of the payment.
 * @param orderId UUID of the related order.
 * @param time    time payment was created in database.
 * @param status  current payment status.
 * @param amount  amount to be paid (or that has been paid if complete)
 */
@Schema(
        description = "Data transfer object for a payment",
        example = """
                {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    "time": "2025-03-13T18:39:00Z",
                    "status": "COMPLETED",
                    "amount": 199.0
                }
                """
)
public record PaymentDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("time") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime time,
        @JsonProperty("status") PaymentStatus status,
        @JsonProperty("amount") double amount
) {

    /**
     * Static method to convert an {@link Payment} model to an {@link PaymentDto}.
     *
     * @param payment {@link Payment} to convert.
     * @return {@link PaymentDto}.
     */
    public static PaymentDto fromModel(Payment payment) {
        return new PaymentDto(
                payment.id(),
                payment.orderId(),
                payment.time(),
                payment.status(),
                payment.amount()
        );
    }
}
