package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.MockFactory;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class BaseRepositoryTest<D, R extends MongoRepository<D, String>> {

    private final R repository;
    private final File testDataFile;
    private final String componentId;
    private final String entityType;
    private int documentSize;


    public BaseRepositoryTest(R repository, File testDataFile, String componentId, String entityType) {
        this.repository = repository;
        this.testDataFile = testDataFile;
        this.componentId = componentId;
        this.entityType = entityType;
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
    protected abstract String getComponentId(D component);
    protected abstract D getNewEntity();

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

    @Test
    @DisplayName("findAll: return all the entities defined in the test file - Success")
    void findAllReturnsSuccess() {
        List<D> prospects = getRepository().findAll();

        Assertions.assertEquals(
                getDocumentSize(),
                prospects.size(),
                String.format("Should be %d entities in the database", getDocumentSize()));
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String COMPONENT_ID = componentId;
        Optional<D> entity = getRepository().findById(COMPONENT_ID);

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
        Optional<D> user = getRepository().findById(COMPONENT_ID);

        Assertions.assertFalse(
                user.isPresent(),
                String.format("Entity [%s] with [id]: %s can be found", entityType, COMPONENT_ID));
    }

    @Test
    @DisplayName("save: entity saved - Success")
    void saveWhenAddNewEntityReturnsSuccess() {
        D newEntity = getRepository().insert(getNewEntity());
        List<D> entities = getRepository().findAll();

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
