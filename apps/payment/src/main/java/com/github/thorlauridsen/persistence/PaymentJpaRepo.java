package com.github.thorlauridsen.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Payment repository interface.
 * This is a JPA repository for the PaymentEntity.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
@Repository
public interface PaymentJpaRepo extends JpaRepository<PaymentEntity, UUID> {

    /**
     * Find a payment by order id.
     *
     * @param orderId UUID of the order.
     * @return {@link Optional} of {@link PaymentEntity}.
     */
    Optional<PaymentEntity> findByOrderId(UUID orderId);
}
