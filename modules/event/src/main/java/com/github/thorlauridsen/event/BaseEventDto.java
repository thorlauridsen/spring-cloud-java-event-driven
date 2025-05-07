package com.github.thorlauridsen.event;

import com.github.thorlauridsen.model.enumeration.EventType;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;

/**
 * Abstract class for all events.
 * This contains common fields for all events.
 */
@Getter
public abstract class BaseEventDto {
    protected final UUID id;
    private final OffsetDateTime time;
    private final EventType eventType;

    /**
     * Constructor for BaseEvent.
     *
     * @param id        UUID of the event.
     * @param eventType type of event.
     */
    protected BaseEventDto(
            UUID id,
            EventType eventType
    ) {
        this.id = id;
        this.time = OffsetDateTime.now();
        this.eventType = eventType;
    }
}
