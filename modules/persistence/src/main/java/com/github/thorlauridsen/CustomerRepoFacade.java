package com.github.thorlauridsen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Customer repository facade class.
 * <p>
 * This class is a facade for the {@link CustomerRepo}.
 * A service class can use this facade to easily interact with the
 * repository without needing to know about the database entity {@link CustomerEntity}.
 * <p>
 * It is annotated with {@link Repository} to allow Spring to automatically
 * detect it as a bean and inject it where needed.
 */
@Repository
public class CustomerRepoFacade {

    private final CustomerRepo customerRepo;
    private final Logger logger = LoggerFactory.getLogger(CustomerRepoFacade.class);

    /**
     * Constructor for customer repository facade.
     *
     * @param customerRepo {@link CustomerRepo}.
     */
    public CustomerRepoFacade(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    /**
     * Save a customer.
     * This will create a new {@link CustomerEntity} and save it to the database.
     *
     * @param customerInput Input object for creating a customer.
     * @return {@link Customer} model class.
     */
    public Customer save(CustomerInput customerInput) {
        logger.info("Saving customer with mail: {}", customerInput.mail());

        var customer = new CustomerEntity(customerInput.mail());
        var createdCustomer = customerRepo.save(customer);
        logger.info("Customer saved with id: {}", createdCustomer.getId());

        return new Customer(
                createdCustomer.getId(),
                createdCustomer.getMail()
        );
    }

    /**
     * Find a customer by id.
     * This method will convert the {@link CustomerEntity} to a {@link Customer} model.
     *
     * @param id UUID of the customer.
     * @return {@link Optional} of {@link Customer}.
     */
    public Optional<Customer> findById(UUID id) {
        logger.info("Finding customer with id: {}", id);
        var customer = customerRepo.findById(id);

        logger.info("Found customer with id: {}", id);
        return customer.map(customerEntity -> new Customer(
                customerEntity.getId(),
                customerEntity.getMail()
        ));
    }
}
