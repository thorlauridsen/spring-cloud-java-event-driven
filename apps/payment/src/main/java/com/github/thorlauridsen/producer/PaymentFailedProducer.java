package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.PaymentFailedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer for the {@link PaymentFailedEvent}.
 * This class will publish the specific event to the SNS topic.
 */
@Service
public class PaymentFailedProducer extends EventProducer<PaymentFailedEvent> {

    /**
     * Constructor for PaymentFailedProducer.
     *
     * @param snsTemplate {@link SnsTemplate} to publish the event to the SNS topic.
     */
    public PaymentFailedProducer(SnsTemplate snsTemplate) {
        super(snsTemplate, "arn:aws:sns:us-east-1:000000000000:payment-failed-topic");
    }
}
