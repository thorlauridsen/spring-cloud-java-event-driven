package com.github.thorlauridsen.outbox;

import com.github.thorlauridsen.model.event.BaseEvent;
import com.github.thorlauridsen.model.event.OutboxEvent;
import com.github.thorlauridsen.model.repository.IOutboxEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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
@RequiredArgsConstructor
@Slf4j
public class OutboxEventRepo implements IOutboxEventRepo {

    private final JsonMapper jsonMapper;
    private final OutboxEventJpaRepo jpaRepo;

    /**
     * Save an event to the outbox table in the database.
     * First, the event will be serialized to JSON.
     * Then it will be saved to the database.
     *
     * @param event {@link BaseEvent} to save.
     */
    @Override
    public void save(BaseEvent event) {

        val json = jsonMapper.writeValueAsString(event);
        val outboxEvent = new OutboxEvent(
                event.getId(),
                event.getEventType(),
                json,
                OffsetDateTime.now(),
                false
        );
        val outboxEntity = OutboxEventEntity.fromModel(outboxEvent);
        val saved = jpaRepo.save(outboxEntity);
        log.info("Saved outbox event: {} {}", saved.getEventType(), saved.getEventId());
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
        val found = jpaRepo.findById(eventId);
        if (found.isEmpty()) {
            log.warn("Could not find outbox event with ID: {}", eventId);
            return;
        }
        val updated = OutboxEventEntity.markProcessed(found.get());
        jpaRepo.save(updated);
        log.info("Marked outbox event as processed: {}", eventId);
    }
}
