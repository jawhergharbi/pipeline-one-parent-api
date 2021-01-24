package com.sawoo.pipeline.api.repository.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.mock.MockFactory;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class BaseRepositoryTest<M, R extends MongoRepository<M, String>, F extends MockFactory<?, M>> {

    private final R repository;
    private final F mockFactory;
    private final String dataFileName;
    private final String componentId;
    private final String entityType;
    private int documentSize;


    public BaseRepositoryTest(R repository, String dataFileName, String componentId, String entityType, F mockFactory) {
        this.repository = repository;
        this.dataFileName = dataFileName;
        this.componentId = componentId;
        this.entityType = entityType;
        this.mockFactory = mockFactory;
    }

    protected abstract Class<M[]> getClazz();
    protected abstract String getComponentId(M component);
    protected abstract M getNewEntity();

    @BeforeEach
    protected void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Test data file
        File testDataFile = Paths.get("src", "test", "resources", "test-data", dataFileName).toFile();

        // Deserialize our JSON file to an array of reviews
        M[] entityList = mapper.readValue(testDataFile, getClazz());
        documentSize = entityList.length;

        // Load each entity into the DB
        repository.insert(Arrays.asList(entityList));
    }

    @AfterEach
    protected void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
    }

    @Test
    @DisplayName("findAll: return all the entities defined in the test file - Success")
    void findAllReturnsSuccess() {
        List<M> entities = getRepository().findAll();

        Assertions.assertEquals(
                getDocumentSize(),
                entities.size(),
                String.format("Should be %d entities in the database", getDocumentSize()));
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String COMPONENT_ID = componentId;
        Optional<M> entity = getRepository().findById(COMPONENT_ID);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("[%s] with [id]: %s can not be null", entityType, COMPONENT_ID));
        Assertions.assertEquals(
                COMPONENT_ID,
                getComponentId(entity.get()),
                String.format("Entity id for entity type [%s] has to be [%s]", entityType, COMPONENT_ID));
    }

    @Test
    @DisplayName("findById: entity not found -  Failure")
    void findByIdWhenEntityNotFoundReturnsFailure() {
        String COMPONENT_ID = "wrong_id";
        Optional<M> user = getRepository().findById(COMPONENT_ID);

        Assertions.assertFalse(
                user.isPresent(),
                String.format("Entity [%s] with [id]: %s can be found", entityType, COMPONENT_ID));
    }

    @Test
    @DisplayName("insert: entity saved - Success")
    void insertWhenAddNewEntityReturnsSuccess() {
        M newEntity = getRepository().insert(getNewEntity());
        List<M> entities = getRepository().findAll();

        Assertions.assertNotNull(
                getComponentId(newEntity),
                String.format("%s id can not be null for a new inserted document", entityType));

        Assertions.assertEquals(
                getDocumentSize() + 1,
                entities.size(),
                String.format(
                        "Number of entities of type [%s] stored in the collection must be equal to %d",
                        entityType,
                        getDocumentSize() + 1));
    }
}
