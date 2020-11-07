package com.sawoo.pipeline.api.repository;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.MockFactory;

public abstract class BaseRepositoryTest {

    protected final Faker FAKER = Faker.instance();
    private MockFactory mockFactory;

    protected MockFactory getMockFactory() {
        if (mockFactory == null) {
            mockFactory = new MockFactory();
        }
        return mockFactory;
    }
}
