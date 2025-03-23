package com.github.thorlauridsen.deduplication;

import com.github.thorlauridsen.model.event.ProcessedEvent;
import com.github.thorlauridsen.model.repository.IProcessedEventRepo;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Service for deduplicating events.
 * This service is used to check if an event is a duplicate and to record events as processed.
 */
@Service
public class DeduplicationService {

    private final IProcessedEventRepo processedEventRepo;

    /**
     * Constructor for DeduplicationService.
     *
     * @param processedEventRepo {@link IProcessedEventRepo} for checking if an event has already been processed.
     */
    public DeduplicationService(IProcessedEventRepo processedEventRepo) {
        this.processedEventRepo = processedEventRepo;
    }

    /**
     * Check if an event is a duplicate.
     *
     * @param eventId UUID of the event to check.
     * @return true if the event is a duplicate, false otherwise.
     */
    public boolean isDuplicate(UUID eventId) {
        return processedEventRepo.existsById(eventId);
    }

    /**
     * Record an event as processed.
     *
     * @param eventId UUID of the event to record.
     */
    public void record(UUID eventId) {
        var processedEvent = new ProcessedEvent(
                eventId,
                OffsetDateTime.now()
        );
        processedEventRepo.save(processedEvent);
    }
}
