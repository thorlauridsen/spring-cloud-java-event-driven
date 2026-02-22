package com.github.thorlauridsen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * This is the BaseMockMvc class that allows you to send and test HTTP requests.
 */
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    /**
     * Test an HTTP GET request.
     *
     * @param getUrl the URL to send an HTTP GET request to.
     * @return {@link RestTestClient.ResponseSpec} response.
     */
    public RestTestClient.ResponseSpec get(String getUrl) {
        return restTestClient.get()
                .uri(getUrl)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchange();
    }
}
