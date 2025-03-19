package com.github.thorlauridsen.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Payment repository interface.
 * This is a JPA repository for the PaymentEntity.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
@Repository
public interface PaymentRepo extends JpaRepository<PaymentEntity, UUID> {
}
