package com.github.thorlauridsen.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Payment repository interface.
 * This is a JPA repository for the OrderEntity.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
@Repository
public interface OrderJpaRepo extends JpaRepository<OrderEntity, UUID> {
}
