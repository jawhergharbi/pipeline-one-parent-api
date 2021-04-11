package com.sawoo.pipeline.api.repository.base;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseMongoRepository<M> extends MongoRepository<M, String> {

    List<M> deleteByIdIn(List<String> ids);
}
