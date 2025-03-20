package com.github.thorlauridsen.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Outbox repository interface.
 * This is a JPA repository for the OutboxEntity.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
@Repository
public interface OutboxRepo extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findAllByProcessedFalse();
}
