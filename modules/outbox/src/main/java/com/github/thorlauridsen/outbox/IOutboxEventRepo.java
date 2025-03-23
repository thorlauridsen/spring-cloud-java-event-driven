package com.github.thorlauridsen.outbox;

import com.github.thorlauridsen.model.event.BaseEvent;
import com.github.thorlauridsen.model.event.OutboxEvent;
import java.util.List;
import java.util.UUID;

/**
 * Outbox repository interface.
 * This is an interface containing methods for interacting with the outbox table.
 * A repository class will implement this interface to provide the actual implementation.
 * This interface makes it easier to swap out the implementation of the repository if needed.
 */
public interface IOutboxEventRepo {

    /**
     * Save an event to the outbox table in the database.
     *
     * @param event {@link BaseEvent} to save.
     */
    void save(BaseEvent event);

    /**
     * Find all unprocessed outbox events.
     * This will return a list of all outbox events where the processed flag is false.
     *
     * @return List of {@link OutboxEvent}.
     */
    List<OutboxEvent> findAllByProcessedFalse();

    /**
     * Mark an event as processed.
     * This will update the processed flag for the event with the given id to true.
     *
     * @param eventId {@link UUID} of the event to mark as processed.
     */
    void markAsProcessed(UUID eventId);
}
