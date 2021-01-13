package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import lombok.Getter;

public abstract class BaseMockFactory<D, M> implements MockFactory<D, M> {
    @Getter
    private final Faker FAKER = Faker.instance();
}
