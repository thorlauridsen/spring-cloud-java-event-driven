package com.github.thorlauridsen.model.repository;

import com.github.thorlauridsen.model.event.ProcessedEvent;
import java.util.UUID;

/**
 * Processed event repository interface.
 * This is an interface containing methods for interacting with the processed event table.
 * A repository class will implement this interface to provide the actual implementation.
 * This interface makes it easier to swap out the implementation of the repository if needed.
 */
public interface IProcessedEventRepo {

    /**
     * Check if an event exists by id.
     *
     * @param eventId UUID of the event.
     * @return boolean true if the event exists, false otherwise.
     */
    boolean existsById(UUID eventId);

    /**
     * Save a processed event to the database.
     *
     * @param processedEvent {@link ProcessedEvent} to save.
     * @return {@link ProcessedEvent} model class.
     */
    ProcessedEvent save(ProcessedEvent processedEvent);
}
