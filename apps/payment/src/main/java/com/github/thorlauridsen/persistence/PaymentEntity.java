package com.github.thorlauridsen.persistence;

import com.github.thorlauridsen.enumeration.PaymentStatus;
import com.github.thorlauridsen.model.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Class representing the database entity for payments to be saved in the "payment" table.
 */
@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private OffsetDateTime time;

    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private double amount;

    /**
     * Empty default constructor required by JPA.
     */
    protected PaymentEntity() {

    }

    /**
     * Constructor for creating a new instance of PaymentEntity.
     * This contains only the necessary fields for creating
     * a new instance to be saved in the database.
     */
    public PaymentEntity(
            UUID orderId,
            PaymentStatus status,
            double amount
    ) {
        this.orderId = orderId;
        this.time = OffsetDateTime.now();
        this.status = status;
        this.amount = amount;
    }

    /**
     * Method to convert {@link PaymentEntity} to {@link Payment} model.
     *
     * @return {@link Payment}.
     */
    public Payment toModel() {
        return new Payment(
                id,
                orderId,
                time,
                status,
                amount
        );
    }
}
