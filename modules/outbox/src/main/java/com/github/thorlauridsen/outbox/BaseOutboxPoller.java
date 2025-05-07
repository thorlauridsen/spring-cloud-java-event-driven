package com.github.thorlauridsen.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.model.event.OutboxEvent;
import com.github.thorlauridsen.model.repository.IOutboxEventRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Abstract class representing an outbox poller.
 * This contains common logic for all outbox pollers.
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
@RequiredArgsConstructor
@Slf4j
public abstract class BaseOutboxPoller {

    protected final ObjectMapper objectMapper;
    protected final IOutboxEventRepo outboxEventRepo;

    /**
     * Polls the outbox table every 5 seconds and processes unprocessed events.
     * Find all events that have not been processed yet and process them.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollOutboxTable() {
        val events = outboxEventRepo.findAllByProcessedFalse();

        if (events.isEmpty()) {
            return;
        }
        log.info("Found {} unprocessed events. Processing...", events.size());

        for (val event : events) {
            process(event);
        }
    }

    /**
     * Abstract method for processing an unprocessed event.
     * This method must be implemented for any class that extends BaseOutboxPoller.
     *
     * @param event {@link OutboxEvent} event to be processed.
     */
    protected abstract void process(OutboxEvent event);
}
