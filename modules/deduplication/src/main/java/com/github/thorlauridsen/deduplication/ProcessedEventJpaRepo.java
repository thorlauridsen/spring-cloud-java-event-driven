package com.github.thorlauridsen.deduplication;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Processed event repository interface.
 * This is a JPA repository for the {@link ProcessedEventEntity}.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
public interface ProcessedEventJpaRepo extends JpaRepository<ProcessedEventEntity, UUID> {
}
