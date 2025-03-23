package com.github.thorlauridsen.model.repository;

import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import java.util.Optional;
import java.util.UUID;

/**
 * Order repository interface.
 * This is an interface containing methods for interacting with the order table.
 * A repository class will implement this interface to provide the actual implementation.
 * This interface makes it easier to swap out the implementation of the repository if needed.
 */
public interface IOrderRepo {

    /**
     * Create a new order.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    Order create(OrderCreate order);

    /**
     * Update an existing order.
     *
     * @param order {@link Order} to update.
     * @return {@link Order}.
     */
    Order update(Order order);

    /**
     * Find an order by its id.
     *
     * @param id {@link UUID} of the order to find.
     * @return {@link Optional} of {@link Order}.
     */
    Optional<Order> findById(UUID id);
}
