package com.github.thorlauridsen.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Abstract class for all events.
 * This contains common fields for all events.
 */
public abstract class BaseEvent {
    private final UUID id;
    private final OffsetDateTime time;
    private final EventType eventType;

    /**
     * Constructor for BaseEvent.
     *
     * @param id        UUID of the event.
     * @param eventType type of event.
     */
    protected BaseEvent(
            UUID id,
            EventType eventType
    ) {
        this.id = id;
        this.time = OffsetDateTime.now();
        this.eventType = eventType;
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public EventType getEventType() {
        return eventType;
    }
}
