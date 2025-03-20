package com.github.thorlauridsen.model;

/**
 * Record class representing the fields necessary for creating an order.
 *
 * @param product description of product.
 * @param amount  amount to be paid.
 */
public record OrderCreate(
        String product,
        double amount
) {
}
