package com.github.thorlauridsen.model.event;

import com.github.thorlauridsen.enumeration.EventType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Record class representing the fields necessary for an outbox event.
 *
 * @param eventId   UUID of the event.
 * @param eventType type of the event.
 * @param payload   JSON payload of the event.
 * @param createdAt time the event was created in the database.
 * @param processed whether the event has been processed.
 */
public record OutboxEvent(
        UUID eventId,
        EventType eventType,
        String payload,
        OffsetDateTime createdAt,
        Boolean processed
) {
}
