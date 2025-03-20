package com.github.thorlauridsen.deduplication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

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

    public ProcessedEventEntity(UUID eventId) {
        this.eventId = eventId;
        this.processedAt = OffsetDateTime.now();
    }
}
