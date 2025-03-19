package com.github.thorlauridsen.outbox;

import com.github.thorlauridsen.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

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
@Table(name = "outbox")
public class OutboxEntity {

    /**
     * Unique identifier for the outbox entity.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Unique identifier for the event.
     */
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
     * Empty default constructor required by JPA.
     */
    protected OutboxEntity() {
    }

    /**
     * Constructor for creating a new instance of OutboxEntity.
     * This contains only the necessary fields for creating
     * a new instance to be saved in the database.
     */
    public OutboxEntity(
            UUID eventId,
            EventType eventType,
            String payload
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
    }

    /**
     * Constructor for copying an OutboxEntity while possibly modifying a field.
     * This is for example used in the markProcessed() method.
     * This constructor is set to private to avoid other classes
     */
    private OutboxEntity(
            UUID id,
            UUID eventId,
            EventType eventType,
            String payload,
            OffsetDateTime createdAt,
            boolean processed
    ) {
        this.id = id;
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
     * @param outbox {@link OutboxEntity}
     * @return {@link OutboxEntity} with processed as true.
     */
    public static OutboxEntity markProcessed(OutboxEntity outbox) {
        return new OutboxEntity(
                outbox.getId(),
                outbox.getEventId(),
                outbox.getEventType(),
                outbox.getPayload(),
                outbox.getCreatedAt(),
                true
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
