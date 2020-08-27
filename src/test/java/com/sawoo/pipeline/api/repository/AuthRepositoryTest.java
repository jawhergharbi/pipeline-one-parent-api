package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.model.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AuthRepositoryTest {

    private static final File AUTHENTICATION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "auth-test-data.json").toFile();
    private static final List<String> authIdList = new ArrayList<>();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private AuthRepository repository;


    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Authentication[] authList = mapper.readValue(AUTHENTICATION_JSON_DATA, Authentication[].class);

        // Load each auth entity into the dataStore
        Arrays.stream(authList).forEach((auth) -> {
            authIdList.add(auth.getId());
            datastoreTemplate.save(auth);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAllById(authIdList, Authentication.class);
    }

    @Test
    void findAllWhenTwoEtitiesFoundReturnsSuccess() {
        Iterable<Authentication> auths = repository.findAll();
        Assertions.assertEquals(
                2,
                StreamSupport
                        .stream(auths.spliterator(), false)
                        .collect(Collectors.toList()).size(),
                "Should be 2 Auth entities in the database");
    }

    @Test
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String AUTH_ID = "auth_1";
        Optional<Authentication> authentication = repository.findById(AUTH_ID);

        Assertions.assertTrue(authentication.isPresent(), String.format("Authentication with [id]: %s can not be null", AUTH_ID));
        Assertions.assertEquals(AUTH_ID, authentication.get().getId(), String.format("Authentication [id] must be %s", AUTH_ID));
    }

    @Test
    void findByIdWhenEntitNotFoundReturnsSuccess() {
        String AUTH_ID = "wrong_id";
        Optional<Authentication> authentication = repository.findById(AUTH_ID);

        Assertions.assertFalse(authentication.isPresent(), String.format("Authentication with [id]: %s can be found", AUTH_ID));
    }

    @Test
    void findByIdentifierWhenEntityIdFoundReturnsSuccess() {
        String AUTH_IDENTIFIER = "my_user_identifier_1";
        Optional<Authentication> authentication = repository.findByIdentifier(AUTH_IDENTIFIER);

        Assertions.assertTrue(authentication.isPresent(), String.format("Authentication with [identifier]: %s can not be null", AUTH_IDENTIFIER));
        Assertions.assertEquals(AUTH_IDENTIFIER, authentication.get().getIdentifier(), String.format("Authentication [identifier] must be %s", AUTH_IDENTIFIER));
    }

    @Test
    void findByIdentifierWhenEntityNotFoundReturnsSuccess() {
        String AUTH_IDENTIFIER = "wrong_identifier";
        Optional<Authentication> authentication = repository.findByIdentifier(AUTH_IDENTIFIER);

        Assertions.assertFalse(authentication.isPresent(), String.format("Authentication with [identifier]: %s can be found", AUTH_IDENTIFIER));
    }

    @Test
    void saveWhenAddNewEntityReturnsSuccess() {
        Authentication auth = new Authentication();
        auth.setId(Faker.instance().idNumber().valid());
        auth.setPassword(Faker.instance().internet().password());
        auth.setIdentifier(Faker.instance().internet().emailAddress());
        auth.setProviderType(0);
        auth.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        auth.setSignedUp(LocalDateTime.of(2020, 12, 31, 0, 0));

        authIdList.add(auth.getId());
        repository.save(auth);

        Iterable<Authentication> auths = repository.findAll();
        Assertions.assertEquals(
                3,
                StreamSupport
                        .stream(auths.spliterator(), false)
                        .collect(Collectors.toList()).size(),
                "Should be 3 Auth entities in the database");
    }
}
