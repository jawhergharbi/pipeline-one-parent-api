package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;

public interface MockFactory<D, M> {

    String getComponentId();

    M newEntity(String id);

    D newDTO(String id);

    Faker getFAKER();
}
