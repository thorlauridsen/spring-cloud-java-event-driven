package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


/**
 * Order repository facade class.
 * <p>
 * This class is a facade for the {@link OrderRepo}.
 * A service class can use this facade to easily interact with the
 * repository without needing to know about the database entity {@link OrderEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to automatically
 * detect it as a bean and inject it where needed.
 */
@Repository
public class OrderRepoFacade {

    private final OrderRepo repo;

    /**
     * Constructor for OrderRepoFacade.
     *
     * @param repo {@link OrderRepo} for directly interacting with the order table.
     */
    public OrderRepoFacade(OrderRepo repo) {
        this.repo = repo;
    }

    /**
     * Create a new order with OrderStatus.CREATED.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    public Order create(OrderCreate order) {
        var entity = new OrderEntity(
                OrderStatus.CREATED,
                order.product(),
                order.amount()
        );
        var saved = repo.save(entity);
        return saved.toModel();
    }

    /**
     * Update an existing order.
     *
     * @param order {@link Order} to update.
     * @return {@link Order}.
     */
    public Order update(Order order) {
        var entity = OrderEntity.fromModel(order);
        var saved = repo.save(entity);
        return saved.toModel();
    }

    /**
     * Find an order by id.
     *
     * @param id UUID of the order.
     * @return {@link Optional} of {@link Order}.
     */
    public Optional<Order> findById(UUID id) {
        var found = repo.findById(id);
        return found.map(OrderEntity::toModel);
    }
}
