package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * SNS notification record class.
 *
 * @param type              Type such as "Notification".
 * @param messageId         UUID of the message.
 * @param topicArn          ARN of the topic.
 * @param message           Message as string, containing the JSON object to be deserialized.
 * @param timestamp         Timestamp in format 2025-03-19T07:43:55.744Z.
 * @param unsubscribeURL    URL to unsubscribe from the topic.
 * @param messageAttributes Map of message attributes.
 * @param signatureVersion  Signature version such as "1".
 * @param signature         Signature as string.
 * @param signingCertURL    URL to the signing certificate.
 */
public record SnsNotification(
        @JsonProperty("Type") String type,
        @JsonProperty("MessageId") UUID messageId,
        @JsonProperty("TopicArn") String topicArn,
        @JsonProperty("Message") String message,
        @JsonProperty("Timestamp") OffsetDateTime timestamp,
        @JsonProperty("UnsubscribeURL") String unsubscribeURL,
        @JsonProperty("MessageAttributes") Map<String, SnsMessageAttribute> messageAttributes,
        @JsonProperty("SignatureVersion") String signatureVersion,
        @JsonProperty("Signature") String signature,
        @JsonProperty("SigningCertURL") String signingCertURL
) {
}
