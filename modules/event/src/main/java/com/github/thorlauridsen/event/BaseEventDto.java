package com.github.thorlauridsen.event;

import com.github.thorlauridsen.model.enumeration.EventType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Abstract class for all events.
 * This contains common fields for all events.
 */
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

    /**
     * Get the UUID of the event.
     *
     * @return UUID of the event.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get the time of the event.
     *
     * @return time of the event.
     */
    public OffsetDateTime getTime() {
        return time;
    }

    /**
     * Get the type of the event.
     *
     * @return type of the event.
     */
    public EventType getEventType() {
        return eventType;
    }
}
