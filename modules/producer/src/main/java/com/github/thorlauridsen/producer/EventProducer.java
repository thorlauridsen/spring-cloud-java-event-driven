package com.github.thorlauridsen.producer;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for an event producer.
 * This contains common logic for all event producers.
 *
 * @param <T>
 */
public abstract class EventProducer<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SnsTemplate snsTemplate;
    private final String topicArn;

    /**
     * Constructor for EventProducer.
     *
     * @param snsTemplate The {@link SnsTemplate} to use for producing events.
     * @param topicArn    The ARN of the SNS topic to which messages will be sent.
     */
    public EventProducer(
            SnsTemplate snsTemplate,
            String topicArn
    ) {
        this.snsTemplate = snsTemplate;
        this.topicArn = topicArn;
    }

    /**
     * Publish the event.
     *
     * @param event The event of type {@link T} to publish.
     */
    public void publish(T event) {
        snsTemplate.convertAndSend(topicArn, event);
        logger.info("Sent event: {}", event);
    }
}
