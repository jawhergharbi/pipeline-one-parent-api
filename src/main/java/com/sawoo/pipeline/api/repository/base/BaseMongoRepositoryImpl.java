package com.sawoo.pipeline.api.repository.base;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.List;

public class BaseMongoRepositoryImpl<M> extends SimpleMongoRepository<M, String> implements BaseMongoRepository<M> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<M, String> metadata;

    public BaseMongoRepositoryImpl(MongoEntityInformation<M, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.metadata = metadata;
    }

    @Override
    public List<M> deleteByIdIn(List<String> ids) {
        Criteria criteria = Criteria.where("id").in(ids);
        return mongoOperations.findAllAndRemove(new Query(criteria), metadata.getJavaType());
    }
}
