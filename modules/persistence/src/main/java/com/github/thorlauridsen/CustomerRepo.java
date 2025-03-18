package com.github.thorlauridsen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Customer repository interface.
 * This is a JPA repository for the customer entity.
 * It extends the {@link JpaRepository} interface which allows us to easily define CRUD methods.
 */
public interface CustomerRepo extends JpaRepository<CustomerEntity, UUID> {

    /**
     * Find a customer by id.
     *
     * @param id UUID of the customer.
     * @return {@link Optional} of {@link CustomerEntity}.
     */
    @NonNull Optional<CustomerEntity> findById(@NonNull UUID id);
}
