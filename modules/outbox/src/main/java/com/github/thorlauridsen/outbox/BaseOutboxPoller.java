package com.github.thorlauridsen.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class BaseOutboxPoller {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final ObjectMapper objectMapper;
    protected final OutboxRepo outboxRepo;

    /**
     * Constructor for BaseOutboxPoller.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param outboxRepo   JpaRepository {@link OutboxRepo} for directly interacting with the outbox table.
     */
    public BaseOutboxPoller(
            ObjectMapper objectMapper,
            OutboxRepo outboxRepo
    ) {
        this.objectMapper = objectMapper;
        this.outboxRepo = outboxRepo;
    }

    /**
     * Polls the outbox table every 5 seconds and processes unprocessed events.
     * Find all events that have not been processed yet and process them.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollOutboxTable() {
        var events = outboxRepo.findAllByProcessedFalse();

        if (events.isEmpty()) {
            return;
        }
        logger.info("Found {} unprocessed events. Processing...", events.size());

        for (OutboxEntity event : events) {
            process(event);
        }
    }

    /**
     * Abstract method for processing an unprocessed event.
     * This method must be implemented for any class that extends BaseOutboxPoller.
     *
     * @param event {@link OutboxEntity} event to be processed.
     */
    protected abstract void process(OutboxEntity event);
}
