package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Class representing the database entity for orders to be saved in the "product_order" table.
 * We cannot use "order" as a table name as that is a reserved keyword.
 */
@Entity
@Table(name = "product_order")
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private OffsetDateTime time;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private double amount;

    /**
     * Empty default constructor required by JPA.
     */
    protected OrderEntity() {

    }

    /**
     * Constructor for creating a new instance of OrderEntity.
     * This contains only the necessary fields for creating
     * a new instance to be saved in the database.
     */
    public OrderEntity(
            OrderStatus status,
            String product,
            double amount
    ) {
        this.time = OffsetDateTime.now();
        this.status = status;
        this.product = product;
        this.amount = amount;
    }

    /**
     * Constructor for creating a new instance given all the provided values.
     * This has been set to private so it can only be used for
     * methods within this class, for example, the fromModel() method.
     *
     * @param id      UUID of the order.
     * @param time    time payment was created in database.
     * @param status  current order status.
     * @param product description of product.
     * @param amount  amount to be paid (or that has been paid if complete).
     */
    private OrderEntity(
            UUID id,
            OffsetDateTime time,
            OrderStatus status,
            String product,
            double amount
    ) {
        this.id = id;
        this.time = time;
        this.status = status;
        this.product = product;
        this.amount = amount;
    }

    /**
     * Static method to convert an {@link Order} model to an {@link OrderEntity}.
     *
     * @param order {@link Order} to convert.
     * @return {@link OrderEntity}.
     */
    public static OrderEntity fromModel(Order order) {
        return new OrderEntity(
                order.id(),
                order.time(),
                order.status(),
                order.product(),
                order.amount()
        );
    }

    /**
     * Method to convert {@link OrderEntity} to {@link Order} model.
     *
     * @return {@link Order}.
     */
    public Order toModel() {
        return new Order(
                id,
                time,
                status,
                product,
                amount
        );
    }
}
