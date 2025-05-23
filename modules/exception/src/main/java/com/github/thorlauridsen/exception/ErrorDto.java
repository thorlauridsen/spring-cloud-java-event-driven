package com.github.thorlauridsen.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

/**
 * Data transfer object for handling errors.
 * Contains the description of the error and the time it occurred.
 *
 * @param description Description of the error.
 * @param time        {@link OffsetDateTime} when the error occurred.
 */
@Schema(
        description = "Data transfer object for an error",
        example = """
                {
                     "description": "Error message",
                     "time": "2025-03-13T18:39:00.4900802Z"
                }
                """
)
public record ErrorDto(
        @JsonProperty("description") String description,
        @JsonProperty("time") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime time
) {
}
