package com.github.thorlauridsen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.dto.CustomerDto;
import com.github.thorlauridsen.dto.CustomerInputDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.github.thorlauridsen.controller.Endpoint.CUSTOMER_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomerControllerTest extends BaseMockMvc {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CustomerControllerTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    public void getCustomer_randomId_returnsNotFound() throws Exception {
        var id = UUID.randomUUID();
        var response = mockGet(CUSTOMER_BASE_ENDPOINT + "/" + id);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice@gmail.com",
            "bob@gmail.com"
    })
    public void postCustomer_getCustomer_success(String mail) throws Exception {
        var customer = new CustomerInputDto(mail);
        var json = objectMapper.writeValueAsString(customer);
        var response = mockPost(json, CUSTOMER_BASE_ENDPOINT);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        var responseJson = response.getContentAsString();
        var createdCustomer = objectMapper.readValue(responseJson, CustomerDto.class);
        assertCustomer(createdCustomer, mail);

        var response2 = mockGet(CUSTOMER_BASE_ENDPOINT + "/" + createdCustomer.id());
        assertEquals(HttpStatus.OK.value(), response2.getStatus());

        var responseJson2 = response2.getContentAsString();
        var fetchedCustomer = objectMapper.readValue(responseJson2, CustomerDto.class);
        assertCustomer(fetchedCustomer, mail);
    }

    /**
     * Ensure that customer is not null and that the id is not null.
     * Assert that the mail is equal to the expected mail.
     *
     * @param customer     {@link CustomerDto}
     * @param expectedMail Expected mail of the customer.
     */
    private void assertCustomer(CustomerDto customer, String expectedMail) {
        assertNotNull(customer);
        assertNotNull(customer.id());
        assertEquals(expectedMail, customer.mail());
    }
}
