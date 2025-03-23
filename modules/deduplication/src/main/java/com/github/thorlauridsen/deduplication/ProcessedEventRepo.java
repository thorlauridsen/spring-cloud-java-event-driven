package com.github.thorlauridsen.deduplication;

import com.github.thorlauridsen.model.event.ProcessedEvent;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Processed event repository class.
 * <p>
 * This class implements the {@link IProcessedEventRepo} interface.
 * It is responsible for interacting with the processed event table in the database.
 * A service class can use this to easily interact with the database
 * without needing to know about the database entity {@link ProcessedEventEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to
 * automatically detect it as a bean and inject it where needed.
 */
@Repository
public class ProcessedEventRepo implements IProcessedEventRepo {

    private final ProcessedEventJpaRepo jpaRepo;

    /**
     * Constructor for ProcessedEventRepo.
     *
     * @param jpaRepo JpaRepository {@link ProcessedEventJpaRepo} for directly interacting with the processed event table.
     */
    public ProcessedEventRepo(ProcessedEventJpaRepo jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /**
     * Check if an event exists by id.
     *
     * @param eventId UUID of the event.
     * @return boolean true if the event exists, false otherwise.
     */
    @Override
    public boolean existsById(UUID eventId) {
        return jpaRepo.existsById(eventId);
    }

    /**
     * Save a processed event to the database.
     *
     * @param event {@link ProcessedEvent} to save.
     * @return {@link ProcessedEvent} model class.
     */
    @Override
    public ProcessedEvent save(ProcessedEvent event) {
        var entity = ProcessedEventEntity.fromModel(event);
        var saved = jpaRepo.save(entity);
        return saved.toModel();
    }
}
