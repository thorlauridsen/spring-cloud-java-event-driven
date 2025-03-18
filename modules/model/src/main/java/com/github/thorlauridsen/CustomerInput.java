package com.github.thorlauridsen;

/**
 * Customer model class for creating a customer.
 * Contains all the fields for creating a customer.
 *
 * @param mail Mail as string of the customer.
 */
public record CustomerInput(
        String mail
) {
}
