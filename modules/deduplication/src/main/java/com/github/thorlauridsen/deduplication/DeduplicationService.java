package com.github.thorlauridsen.deduplication;

import com.github.thorlauridsen.model.event.ProcessedEvent;
import com.github.thorlauridsen.model.repository.IProcessedEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service for deduplicating events.
 * This service is used to check if an event is a duplicate and to record events as processed.
 */
@RequiredArgsConstructor
@Service
public class DeduplicationService {

    private final IProcessedEventRepo processedEventRepo;

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
    public void recordEvent(UUID eventId) {
        val processedEvent = new ProcessedEvent(
                eventId,
                OffsetDateTime.now()
        );
        processedEventRepo.save(processedEvent);
    }
}
