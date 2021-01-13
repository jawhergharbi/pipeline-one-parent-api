package com.sawoo.pipeline.api.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class DummyRepositoryTest {

    private static final File DUMMY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "dummy-test-data.json").toFile();
    private int documentSize;

    @Autowired
    private DummyRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() throws Exception {
        // Deserialize our JSON file to an array of reviews
        DummyEntity[] dummyList = mapper.readValue(DUMMY_JSON_DATA, DummyEntity[].class);
        documentSize = dummyList.length;

        // Load each dummy entity into the datastore
        repository.insert(Arrays.asList(dummyList));
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
    }

    @Test
    void findAllTwoEntitiesFoundReturnsSuccess() {
        List<DummyEntity> dummies = repository.findAll();

        Assertions.assertEquals(
                documentSize,
                dummies.size(),
                String.format("Should be %d dummy entities in the database", documentSize));
    }
}
