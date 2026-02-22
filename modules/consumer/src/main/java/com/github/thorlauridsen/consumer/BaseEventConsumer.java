package com.github.thorlauridsen.consumer;

import com.github.thorlauridsen.event.BaseEventDto;
import com.github.thorlauridsen.event.SnsNotificationDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.databind.json.JsonMapper;

/**
 * Abstract class for an event consumer.
 * This contains common logic for all event consumers.
 *
 * @param <T> The type of event to consume.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseEventConsumer<T extends BaseEventDto> {

    private final JsonMapper jsonMapper;

    /**
     * Listen for messages on the SQS queue.
     * Convert the JSON message to an SNS notification.
     * Deserialize the message and pass it to the processEvent method.
     *
     * @param json The JSON message from the SQS queue as a String.
     */
    @SqsListener
    public void listen(String json) {

        log.debug("Received JSON: {}", json);

        val notification = jsonMapper.readValue(json, SnsNotificationDto.class);
        log.debug("Received SNS notification: {}", notification);

        val eventJson = notification.message();
        T event = jsonMapper.readValue(eventJson, getEventClass());

        log.info("Received event: {} {}", event.getEventType(), event.getId());
        processEvent(event);
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
