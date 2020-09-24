package com.sawoo.pipeline.api.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.StreamSupport;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DummyRepositoryTest {

    private static final File DUMMY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "dummy-test-data.json").toFile();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private DummyRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() throws Exception {
        // Deserialize our JSON file to an array of reviews
        DummyEntity[] dummyList = mapper.readValue(DUMMY_JSON_DATA, DummyEntity[].class);

        // Load each dummy entity into the datastore
        Arrays.stream(dummyList).forEach(datastoreTemplate::save);
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAll(DummyEntity.class);
    }

    @Test
    void findAllTwoEntitiesFoundReturnsSuccess() {
        Iterable<DummyEntity> dummies = repository.findAll();

        Assertions
                .assertEquals(
                2,
                        (int) StreamSupport
                                .stream(dummies.spliterator(), false).count(),
                        "Should be two Dummy entities in the database");
    }
}
