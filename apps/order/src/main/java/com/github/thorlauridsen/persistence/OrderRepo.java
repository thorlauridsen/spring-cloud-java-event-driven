package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.model.repository.IOrderRepo;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
@RequiredArgsConstructor
public class OrderRepo implements IOrderRepo {

    private final OrderJpaRepo jpaRepo;

    /**
     * Create a new order with OrderStatus.CREATED.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    @Override
    public Order create(OrderCreate order) {
        val entity = new OrderEntity(
                OrderStatus.CREATED,
                order.product(),
                order.amount()
        );
        val saved = jpaRepo.save(entity);
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
        val entity = OrderEntity.fromModel(order);
        val saved = jpaRepo.save(entity);
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
        val found = jpaRepo.findById(id);
        return found.map(OrderEntity::toModel);
    }
}
