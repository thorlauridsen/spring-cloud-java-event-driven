package com.github.thorlauridsen.outbox;

import com.github.thorlauridsen.model.enumeration.EventType;
import com.github.thorlauridsen.model.event.OutboxEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class representing the outbox entity for the "outbox" table.
 * The purpose of this is to follow the transactional outbox pattern.
 * <p>
 * With this pattern, we for example avoid saving an order to
 * the database at the same time as publishing an order event.
 * <p>
 * Instead, we for example use this pattern to save an order to the
 * "product_order" table and saved a related event to the "outbox" table.
 * Then we can use a scheduled poller to fetch events to be processed from the "outbox" table.
 * Essentially, a database transaction is completed before events are published.
 */
@Entity
@Getter
@Table(name = "outbox")
@NoArgsConstructor
public class OutboxEventEntity {

    /**
     * Unique identifier for the event.
     */
    @Id
    @Column(nullable = false)
    private UUID eventId;

    /**
     * Event type such as ORDER_CREATED or PAYMENT_COMPLETED.
     */
    @Column(nullable = false)
    private EventType eventType;

    /**
     * Event payload as JSON string.
     */
    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /**
     * Flag to mark if event has been processed.
     */
    @Column(nullable = false)
    private Boolean processed = false;

    /**
     * Constructor for OutboxEntity.
     */
    public OutboxEventEntity(
            UUID eventId,
            EventType eventType,
            String payload,
            OffsetDateTime createdAt,
            boolean processed
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.processed = processed;
    }

    /**
     * Static method to set processed to true.
     * This method is provided to increase immutability.
     *
     * @param outbox {@link OutboxEventEntity}
     * @return {@link OutboxEventEntity} with processed as true.
     */
    public static OutboxEventEntity markProcessed(OutboxEventEntity outbox) {
        return new OutboxEventEntity(
                outbox.getEventId(),
                outbox.getEventType(),
                outbox.getPayload(),
                outbox.getCreatedAt(),
                true
        );
    }

    /**
     * Convert the entity to a model.
     *
     * @return {@link OutboxEvent} model.
     */
    public OutboxEvent toModel() {
        return new OutboxEvent(
                eventId,
                eventType,
                payload,
                createdAt,
                processed
        );
    }

    /**
     * Convert a model to an entity.
     *
     * @param outboxEvent {@link OutboxEvent} model.
     * @return {@link OutboxEventEntity} entity.
     */
    public static OutboxEventEntity fromModel(OutboxEvent outboxEvent) {
        return new OutboxEventEntity(
                outboxEvent.eventId(),
                outboxEvent.eventType(),
                outboxEvent.payload(),
                outboxEvent.createdAt(),
                outboxEvent.processed()
        );
    }
}
