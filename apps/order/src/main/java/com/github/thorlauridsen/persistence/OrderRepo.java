package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Order event repository class.
 * <p>
 * This class implements the {@link IOrderRepo} interface.
 * It is responsible for interacting with the order table in the database.
 * A service class can use this to easily interact with the database
 * without needing to know about the database entity {@link OrderEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to
 * automatically detect it as a bean and inject it where needed.
 */
@Repository
public class OrderRepo implements IOrderRepo {

    private final OrderJpaRepo jpaRepo;

    /**
     * Constructor for OrderRepo.
     *
     * @param jpaRepo JpaRepository {@link OrderJpaRepo} for directly interacting with the order table.
     */
    public OrderRepo(OrderJpaRepo jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    /**
     * Create a new order with OrderStatus.CREATED.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    @Override
    public Order create(OrderCreate order) {
        var entity = new OrderEntity(
                OrderStatus.CREATED,
                order.product(),
                order.amount()
        );
        var saved = jpaRepo.save(entity);
        return saved.toModel();
    }

    /**
     * Update an existing order.
     *
     * @param order {@link Order} to update.
     * @return {@link Order}.
     */
    @Override
    public Order update(Order order) {
        var entity = OrderEntity.fromModel(order);
        var saved = jpaRepo.save(entity);
        return saved.toModel();
    }

    /**
     * Find an order by id.
     *
     * @param id UUID of the order.
     * @return {@link Optional} of {@link Order}.
     */
    @Override
    public Optional<Order> findById(UUID id) {
        var found = jpaRepo.findById(id);
        return found.map(OrderEntity::toModel);
    }
}
