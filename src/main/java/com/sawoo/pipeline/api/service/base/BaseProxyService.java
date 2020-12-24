package com.sawoo.pipeline.api.service.base;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseProxyService<R extends MongoRepository<?, String>, OM> {

    R getRepository();

    OM getMapper();
}
