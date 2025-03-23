package com.github.thorlauridsen.model.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Record class representing the fields necessary for a processed event.
 *
 * @param eventId     UUID of the processed event.
 * @param processedAt time the event was processed.
 */
public record ProcessedEvent(
        UUID eventId,
        OffsetDateTime processedAt
) {
}
