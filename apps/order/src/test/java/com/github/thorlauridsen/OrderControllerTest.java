package com.github.thorlauridsen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static com.github.thorlauridsen.controller.BaseEndpoint.ORDER_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderControllerTest extends BaseMockMvc {

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderControllerTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper
    ) {
        super(mockMvc);
        this.objectMapper = objectMapper;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Computer",
            "Keyboard",
            "Monitor"
    })
    public void postCustomer_getCustomer_success(String product) throws Exception {
        var random = new Random();
        var amount = random.nextDouble(1000);
        var customer = new OrderCreateDto(
                product,
                amount
        );
        var json = objectMapper.writeValueAsString(customer);
        var response = mockPost(json, ORDER_BASE_ENDPOINT + "/create");
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var responseJson = response.getContentAsString();
        var createdOrder = objectMapper.readValue(responseJson, OrderDto.class);

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.id());
        assertEquals(product, createdOrder.product());
        assertEquals(amount, createdOrder.amount());
    }
}
