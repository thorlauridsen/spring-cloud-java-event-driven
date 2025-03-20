package com.github.thorlauridsen.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for FasterXML Jackson.
 * This provides a bean for an {@link ObjectMapper} with {@link JavaTimeModule} registered.
 * <p>
 * This bean is available in an independent subproject so the
 * configuration can be used in any subproject that needs it.
 */
@Configuration
public class JacksonConfig {

    /**
     * Provides an object mapper with JavaTimeModule registered.
     * This can for example be used to serialize OffsetDateTime objects.
     *
     * @return {@link ObjectMapper} with {@link JavaTimeModule} registered.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
