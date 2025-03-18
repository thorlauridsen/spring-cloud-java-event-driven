package com.github.thorlauridsen;

import java.util.UUID;

/**
 * Customer model class.
 * Represents a customer with an id and an email.
 *
 * @param id   UUID of the customer.
 * @param mail Mail as string of the customer.
 */
public record Customer(
        UUID id,
        String mail
) {
}
