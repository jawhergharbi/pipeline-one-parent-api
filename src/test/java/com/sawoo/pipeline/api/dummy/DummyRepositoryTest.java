package com.sawoo.pipeline.api.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class DummyRepositoryTest {

    private static final File DUMMY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "dummy-test-data.json").toFile();
    private int documentSize;
    private Faker FAKER = Faker.instance();


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
    void findAllWhenTwoEntitiesFoundReturnsSuccess() {
        List<DummyEntity> dummies = repository.findAll();

        Assertions.assertEquals(
                documentSize,
                dummies.size(),
                String.format("Should be %d dummy entities in the database", documentSize));
    }

    @Test
    void saveWhenTimeMustBeStoredAndRetrieveUTCSuccess() {
        // Set up mocked entities
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        String DUMMY_ID = FAKER.internet().uuid();
        DummyEntity dummy = DummyEntity.builder()
                .id(DUMMY_ID)
                .name(FAKER.name().name())
                .number(FAKER.number().numberBetween(0, 100))
                .version(1)
                .dateTime(now)
                .build();

        // Execute the repository call
        repository.save(dummy);
        Optional<DummyEntity> retrievedEntity = repository.findById(DUMMY_ID);

        // Assert
        Assertions.assertTrue(retrievedEntity.isPresent(), "Entity must be found");
        Assertions.assertEquals(
                retrievedEntity.get().getDateTime().getHour(),
                now.getHour(),
                String.format("Time from entity %s must be the same retrieve from the database %s", now, retrievedEntity.get().getDateTime()));
    }
}
