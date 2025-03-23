package com.github.thorlauridsen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * This is the BaseMockMvc class which allows you to send and test HTTP requests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BaseMockMvc {

    private final MockMvc mockMvc;

    @Autowired
    public BaseMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Mock an HTTP GET request.
     *
     * @param getUrl The URL to send an HTTP GET request to.
     * @return {@link MockHttpServletResponse} response.
     */
    public MockHttpServletResponse mockGet(String getUrl) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
    }
}
