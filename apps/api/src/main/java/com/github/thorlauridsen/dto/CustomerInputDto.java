package com.github.thorlauridsen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.thorlauridsen.CustomerInput;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object for creating a customer.
 * This is created to ensure that FasterXML Jackson can serialize and deserialize customers.
 * Contains all the fields for creating a customer.
 *
 * @param mail Mail as string of the customer.
 */
@Schema(
        description = "Data transfer object for creating a new customer",
        example = """
                {
                    "mail": "bob@gmail.com"
                }
                """
)
public record CustomerInputDto(
        @JsonProperty("mail") String mail
) {

    /**
     * Converts the DTO to a model.
     *
     * @return {@link CustomerInput} model.
     */
    public CustomerInput toModel() {
        return new CustomerInput(mail());
    }
}
