package com.sawoo.pipeline.api.interagtion.base;

import com.sawoo.pipeline.api.interagtion.EntityExpectedResults;
import lombok.Getter;
import org.springframework.data.mongodb.core.MongoTemplate;

@Getter
public abstract class BaseIntegrationTest<M> {

    private final String resourceURI;
    private final String entityType;
    private final MongoTemplate mongoTemplate;
    private EntityExpectedResults<M> expectedResults;


    public BaseIntegrationTest(MongoTemplate mongoTemplate, String resourceURI, String entityType) {
        this.mongoTemplate = mongoTemplate;
        this.resourceURI = resourceURI;
        this.entityType = entityType;
    }

    protected abstract Class<M[]> getClazz();
}
