package com.sawoo.pipeline.api.controller.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.MockFactory;

public abstract class BaseControllerTest {

    protected final static String FAKER_USER_ID_REGEX = "[a-z1-9]{10}";
    protected final Faker FAKER = Faker.instance();
    private MockFactory mockFactory;

    protected static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MockFactory getMockFactory() {
        if (mockFactory == null) {
            mockFactory = new MockFactory();
        }
        return mockFactory;
    }
}
