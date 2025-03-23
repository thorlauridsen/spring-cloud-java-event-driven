package com.github.thorlauridsen.deduplication;

import com.github.thorlauridsen.model.event.ProcessedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Class representing a processed event.
 * This database entity will be saved in the "processed_event" table.
 * The purpose of this entity is to keep track of events that have already been processed.
 * This is useful for idempotency and to avoid processing the same event multiple times.
 */
@Entity
@Table(name = "processed_event")
public class ProcessedEventEntity {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private OffsetDateTime processedAt;

    /**
     * Empty default constructor required by JPA.
     */
    public ProcessedEventEntity() {
    }

    /**
     * Constructor for ProcessedEventEntity.
     *
     * @param eventId     UUID of the processed event.
     * @param processedAt time the event was processed.
     */
    private ProcessedEventEntity(
            UUID eventId,
            OffsetDateTime processedAt
    ) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    /**
     * Method to convert {@link ProcessedEventEntity} to {@link ProcessedEvent} model.
     *
     * @return {@link ProcessedEvent}.
     */
    public ProcessedEvent toModel() {
        return new ProcessedEvent(eventId, processedAt);
    }

    /**
     * Method to convert {@link ProcessedEvent} model to {@link ProcessedEventEntity}.
     *
     * @param processedEvent {@link ProcessedEvent} model.
     * @return {@link ProcessedEventEntity}.
     */
    public static ProcessedEventEntity fromModel(ProcessedEvent processedEvent) {
        return new ProcessedEventEntity(
                processedEvent.eventId(),
                processedEvent.processedAt()
        );
    }
}
