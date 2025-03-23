package com.github.thorlauridsen.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.model.event.BaseEvent;
import com.github.thorlauridsen.model.event.OutboxEvent;
import com.github.thorlauridsen.model.repository.IOutboxEventRepo;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Outbox event repository class.
 * <p>
 * This class implements the {@link IOutboxEventRepo} interface.
 * It is responsible for interacting with the outbox table in the database.
 * A service class can use this to easily interact with the database
 * without needing to know about the database entity {@link OutboxEventEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to
 * automatically detect it as a bean and inject it where needed.
 */
@Repository
public class OutboxEventRepo implements IOutboxEventRepo {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final OutboxEventJpaRepo jpaRepo;

    /**
     * Constructor for OutboxEventRepo.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param jpaRepo      JpaRepository {@link OutboxEventJpaRepo} for directly interacting with the outbox table.
     */
    public OutboxEventRepo(
            ObjectMapper objectMapper,
            OutboxEventJpaRepo jpaRepo
    ) {
        this.objectMapper = objectMapper;
        this.jpaRepo = jpaRepo;
    }

    /**
     * Save an event to the outbox table in the database.
     * First, the event will be serialized to JSON.
     * Then it will be saved to the database.
     *
     * @param event {@link BaseEvent} to save.
     */
    @Override
    public void save(BaseEvent event) {
        try {
            var json = objectMapper.writeValueAsString(event);
            var outboxEvent = new OutboxEvent(
                    event.getId(),
                    event.getEventType(),
                    json,
                    OffsetDateTime.now(),
                    false
            );
            var outboxEntity = OutboxEventEntity.fromModel(outboxEvent);
            var saved = jpaRepo.save(outboxEntity);
            logger.info("Saved outbox event: {} {}", saved.getEventType(), saved.getEventId());

        } catch (JsonProcessingException ex) {
            logger.error("Failed to save outbox event {} - {}", event.getId(), ex.getMessage(), ex);
        }
    }

    /**
     * Find all unprocessed outbox events.
     * This will return a list of all outbox events where the processed flag is false.
     *
     * @return List of {@link OutboxEvent}.
     */
    @Override
    public List<OutboxEvent> findAllByProcessedFalse() {
        return jpaRepo.findAllByProcessedFalse().stream()
                .map(OutboxEventEntity::toModel)
                .toList();
    }

    /**
     * Mark an event as processed.
     * This will update the processed flag for the event with the given id to true.
     *
     * @param eventId {@link UUID} of the event to mark as processed.
     */
    @Override
    public void markAsProcessed(UUID eventId) {
        var found = jpaRepo.findById(eventId);
        if (found.isEmpty()) {
            logger.warn("Could not find outbox event with ID: {}", eventId);
            return;
        }
        var updated = OutboxEventEntity.markProcessed(found.get());
        jpaRepo.save(updated);
        logger.info("Marked outbox event as processed: {}", eventId);
    }
}
