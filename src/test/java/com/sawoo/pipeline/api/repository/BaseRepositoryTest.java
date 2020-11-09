package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.MockFactory;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.File;
import java.util.Arrays;

@Getter
public abstract class BaseRepositoryTest<D, R extends MongoRepository<D, String>> {

    private final R repository;
    private final File testDataFile;
    private int documentSize;

    public BaseRepositoryTest(R repository, File testDataFile) {
        this.repository = repository;
        this.testDataFile = testDataFile;
    }


    protected final Faker FAKER = Faker.instance();
    private MockFactory mockFactory;

    protected MockFactory getMockFactory() {
        if (mockFactory == null) {
            mockFactory = new MockFactory();
        }
        return mockFactory;
    }

    protected abstract Class<D[]> getClazz();

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        D[] leadList = mapper.readValue(testDataFile, getClazz());
        documentSize = leadList.length;


        // Load each entity into the DB
        repository.insert(Arrays.asList(leadList));
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
    }
}
