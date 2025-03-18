package com.github.thorlauridsen;

import com.github.thorlauridsen.exception.CustomerNotFoundException;
import com.github.thorlauridsen.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for testing the {@link CustomerService}.
 * This class uses the @SpringBootTest annotation to spin up a Spring Boot instance.
 * This ensures that Spring can automatically inject {@link CustomerService} with a {@link CustomerRepo}
 */
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    public void getCustomerWithRandomIdReturnsNotFound() {
        var id = UUID.randomUUID();
        assertThrows(CustomerNotFoundException.class, () -> customerService.findById(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice@gmail.com",
            "bob@gmail.com"
    })
    public void saveCustomerAndGetCustomerSuccess(String mail) throws CustomerNotFoundException {
        var customer = new CustomerInput(mail);

        var savedCustomer = customerService.save(customer);
        assertCustomer(savedCustomer, mail);

        var fetchedCustomer = customerService.findById(savedCustomer.id());
        assertCustomer(fetchedCustomer, mail);
    }

    /**
     * Ensure that customer is not null and that the id is not null.
     * Assert that the mail is equal to the expected mail.
     *
     * @param customer     {@link Customer}
     * @param expectedMail Expected mail of the customer.
     */
    private void assertCustomer(Customer customer, String expectedMail) {
        assertNotNull(customer);
        assertNotNull(customer.id());
        assertEquals(expectedMail, customer.mail());
    }
}
