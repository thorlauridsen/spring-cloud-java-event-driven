package com.github.thorlauridsen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.BaseEvent;
import com.github.thorlauridsen.event.SnsNotification;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Abstract class for an event consumer.
 * This contains common logic for all event consumers.
 *
 * @param <T> The type of event to consume.
 */
public abstract class EventConsumer<T extends BaseEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;

    /**
     * Constructor for EventConsumer.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     */
    public EventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Listen for messages on the SQS queue.
     * Convert the JSON message to an SNS notification.
     * Deserialize the message and pass it to the processEvent method.
     *
     * @param json The JSON message from the SQS queue as a String.
     */
    @SqsListener
    public void listen(String json) {
        try {
            logger.debug("Received JSON: {}", json);

            var notification = objectMapper.readValue(json, SnsNotification.class);
            logger.debug("Received SNS notification: {}", notification);

            var eventJson = notification.message();
            T event = objectMapper.readValue(eventJson, getEventClass());

            logger.info("Received event: {} {}", event.getEventType(), event.getId());
            processEvent(event);

        } catch (IOException e) {
            logger.error("Error deserializing SNS notification: {}", e.getMessage());
        }
    }

    /**
     * This method will be called by subclasses to handle the event processing.
     * It should call the specific service method needed for the event.
     *
     * @param event The event to process.
     */
    protected abstract void processEvent(T event);

    /**
     * Get the class type of the event for the specific consumer.
     * This will allow deserialization of the correct event type.
     *
     * @return The Class type of the event.
     */
    protected abstract Class<T> getEventClass();
}
