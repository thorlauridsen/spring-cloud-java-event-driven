package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an attribute of an SNS notification.
 *
 * @param type  Type of the attribute.
 * @param value Value of the attribute.
 */
public record SnsMessageAttribute(
        @JsonProperty("Type") String type,
        @JsonProperty("Value") String value
) {
}
