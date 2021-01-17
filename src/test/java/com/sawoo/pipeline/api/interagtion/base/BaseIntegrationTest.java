package com.sawoo.pipeline.api.interagtion.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest<M> {

    private final String resourceURI;
    private final String entityType;
    private final MongoTemplate mongoTemplate;
    private final File expectedResultsFileData;

    private Map<String, M> expectedResults;


    public BaseIntegrationTest(MongoTemplate mongoTemplate, String resourceURI, String entityType, File expectedResultsFileData) {
        this.mongoTemplate = mongoTemplate;
        this.resourceURI = resourceURI;
        this.entityType = entityType;
        this.expectedResultsFileData = expectedResultsFileData;
    }

    protected abstract Class<M[]> getClazz();

    @BeforeAll
    protected void beforeAll() throws Exception {
        if (expectedResultsFileData != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            TypeReference<HashMap<String, M>> typeRef
                    = new TypeReference<>() {
            };

            // Deserialize our JSON file to an array of reviews
            expectedResults = mapper.readValue(expectedResultsFileData, typeRef);
        }
    }
}
