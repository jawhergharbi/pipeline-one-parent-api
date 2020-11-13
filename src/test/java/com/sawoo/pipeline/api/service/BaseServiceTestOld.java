package com.sawoo.pipeline.api.service;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.MockFactory;

public abstract class BaseServiceTestOld {

    protected final static String FAKER_USER_ID_REGEX = "[a-z1-9]{10}";
    protected final Faker FAKER = Faker.instance();
    private MockFactory mockFactory;

    protected MockFactory getMockFactory() {
        if (mockFactory == null) {
            mockFactory = new MockFactory();
        }
        return mockFactory;
    }
}
