package com.github.thorlauridsen.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Outbox repository facade class.
 * This provides a simple method for saving any {@link BaseEvent} to the outbox table.
 * <p>
 * This class is a facade for the {@link OutboxRepo}.
 * A service class can use this facade to easily interact with the
 * repository without needing to know about the database entity {@link OutboxEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to automatically
 * detect it as a bean and inject it where needed.
 */
@Repository
public class OutboxRepoFacade {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final OutboxRepo outboxRepo;

    /**
     * Constructor for OutboxRepoFacade.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param outboxRepo   JpaRepository {@link OutboxRepo} for directly interacting with the outbox table.
     */
    public OutboxRepoFacade(
            ObjectMapper objectMapper,
            OutboxRepo outboxRepo
    ) {
        this.objectMapper = objectMapper;
        this.outboxRepo = outboxRepo;
    }

    /**
     * Save an event to the outbox table in the database.
     * First, the event will be serialized to JSON.
     * Then it will be saved to the database.
     *
     * @param event {@link BaseEvent}
     */
    public void save(BaseEvent event) {
        try {
            var json = objectMapper.writeValueAsString(event);
            var outbox = new OutboxEntity(
                    event.getId(),
                    event.getEventType(),
                    json
            );
            var saved = outboxRepo.save(outbox);
            logger.info("Saved outbox event: {}", saved.getId());

        } catch (JsonProcessingException ex) {
            logger.error("Failed to save outbox event {} - {}", event, ex.getMessage(), ex);
        }
    }
}
