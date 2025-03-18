package com.github.thorlauridsen.controller;

import com.github.thorlauridsen.Customer;
import com.github.thorlauridsen.dto.CustomerDto;
import com.github.thorlauridsen.dto.CustomerInputDto;
import com.github.thorlauridsen.exception.CustomerNotFoundException;
import com.github.thorlauridsen.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.UUID;

import static com.github.thorlauridsen.controller.Endpoint.CUSTOMER_BASE_ENDPOINT;

/**
 * Customer controller class.
 * This class implements the {@link ICustomerController} interface and
 * overrides the methods defined in the interface with implementations.
 * The controller is responsible for converting data transfer objects to models and vice versa.
 */
@Controller
public class CustomerController implements ICustomerController {

    private final CustomerService customerService;

    /**
     * Constructor for customer controller.
     *
     * @param customerService {@link CustomerService}.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Save method for customer.
     * Creates the URI location for the newly created customer.
     * Return URI location and customer.
     *
     * @param customerInput Input object for creating a customer.
     * @return {@link ResponseEntity} with {@link CustomerDto}.
     */
    @Override
    public ResponseEntity<CustomerDto> save(CustomerInputDto customerInput) {
        var customer = customerService.save(customerInput.toModel());
        var location = URI.create(CUSTOMER_BASE_ENDPOINT + "/" + customer.id());

        return ResponseEntity.created(location).body(toDto(customer));
    }

    /**
     * Get method for customer.
     * This method returns a customer.
     *
     * @param id UUID of the customer to retrieve.
     * @return {@link ResponseEntity} with {@link CustomerDto}.
     * @throws CustomerNotFoundException if the customer is not found.
     */
    @Override
    public ResponseEntity<CustomerDto> get(UUID id) throws CustomerNotFoundException {
        var customer = customerService.findById(id);
        return ResponseEntity.ok(toDto(customer));
    }

    /**
     * Convert Customer to CustomerDto.
     *
     * @param customer Customer to convert.
     * @return {@link CustomerDto}.
     */
    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(customer.id(), customer.mail());
    }
}
