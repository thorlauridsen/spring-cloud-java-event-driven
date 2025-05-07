package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.BaseEventDto;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for an event producer.
 * This contains common logic for all event producers.
 *
 * @param <T> The type of event to publish.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseEventProducer<T extends BaseEventDto> {

    private final SnsTemplate snsTemplate;
    private final String topicArn;

    /**
     * Publish the event.
     *
     * @param event The event of type {@link T} to publish.
     */
    public void publish(T event) {
        snsTemplate.convertAndSend(topicArn, event);
        log.info("Published event: {} {}", event.getEventType(), event.getId());
    }
}
