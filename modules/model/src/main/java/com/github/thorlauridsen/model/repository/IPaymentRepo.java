package com.github.thorlauridsen.model.repository;

import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.PaymentCreate;
import java.util.Optional;
import java.util.UUID;

/**
 * Payment repository interface.
 * This is an interface containing methods for interacting with the payment table.
 * A repository class will implement this interface to provide the actual implementation.
 * This interface makes it easier to swap out the implementation of the repository if needed.
 */
public interface IPaymentRepo {

    /**
     * Save a payment in the database.
     *
     * @param payment {@link PaymentCreate} to save.
     * @return {@link Payment} model class.
     */
    Payment save(PaymentCreate payment);

    /**
     * Find a payment by order id.
     *
     * @param orderId UUID of the order related to the payment.
     * @return {@link Optional} of {@link Payment}.
     */
    Optional<Payment> findByOrderId(UUID orderId);
}
