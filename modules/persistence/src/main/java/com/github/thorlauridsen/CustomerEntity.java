package com.github.thorlauridsen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Customer entity class.
 * Represents a customer with an id and an email.
 */
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String mail;

    /**
     * Default constructor (required by JPA)
     */
    protected CustomerEntity() {
    }

    /**
     * Constructor for customer.
     *
     * @param mail Mail as string of the customer.
     */
    public CustomerEntity(String mail) {
        this.mail = mail;
    }

    /**
     * Getter for id.
     *
     * @return UUID of the customer.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Getter for mail.
     *
     * @return mail as string of the customer.
     */
    public String getMail() {
        return mail;
    }
}
