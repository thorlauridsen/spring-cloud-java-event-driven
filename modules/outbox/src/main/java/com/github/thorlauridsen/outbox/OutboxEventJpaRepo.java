package com.github.thorlauridsen.outbox;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Outbox repository interface.
 * This is a JPA repository for the {@link OutboxEventEntity}.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
@Repository
public interface OutboxEventJpaRepo extends JpaRepository<OutboxEventEntity, UUID> {

    /**
     * Find all unprocessed outbox events.
     * This will return a list of all outbox events where the processed flag is false.
     *
     * @return List of {@link OutboxEventEntity}.
     */
    List<OutboxEventEntity> findAllByProcessedFalse();
}
