package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.mongo.UserRepositoryMongo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthRepositoryTest extends BaseRepositoryTest {

    private static final File AUTHENTICATION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "user-auth-test-data.json").toFile();
    private int documentSize;
    private static final int ADMIN_USERS = 1;

    @Autowired
    private UserRepositoryMongo repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        UserMongoDB[] userAuthList = mapper.readValue(AUTHENTICATION_JSON_DATA, UserMongoDB[].class);
        documentSize = userAuthList.length;

        // Load each auth entity into the dataStore
        repository.saveAll(Arrays.asList(userAuthList));
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
    }


    @Test
    @DisplayName("findAll: return the entities defined in the file - Success")
    void findAllReturnsSuccess() {
        List<UserMongoDB> users = repository.findAll();
        Assertions.assertEquals(documentSize, users.size());
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String AUTH_ID = "5fa2e7c58b7a2a51f31f2bed";
        Optional<UserMongoDB> user = repository.findById(AUTH_ID);

        Assertions.assertTrue(user.isPresent(), String.format("User with [id]: %s can not be null", AUTH_ID));
        Assertions.assertEquals(AUTH_ID, user.get().getId(), String.format("User [id] must be %s", AUTH_ID));
    }

    @Test
    @DisplayName("findById: entity not found -  Failure")
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        String AUTH_ID = "wrong_id";
        Optional<UserMongoDB> user = repository.findById(AUTH_ID);

        Assertions.assertFalse(user.isPresent(), String.format("User with [id]: %s can be found", AUTH_ID));
    }

    @Test
    @DisplayName("findByEmail: entity found - Success")
    void findByEmailWhenEntityFoundReturnsSuccess() {
        String AUTH_EMAIL = "miguel@gmail.com";
        Optional<UserMongoDB> user = repository.findByEmail(AUTH_EMAIL);

        Assertions.assertTrue(
                user.isPresent(),
                String.format("User with [email]: %s can not be null", AUTH_EMAIL));
        Assertions.assertEquals(
                AUTH_EMAIL,
                user.get().getEmail(),
                String.format("User [identifier] must be %s", AUTH_EMAIL));
    }

    @Test
    @DisplayName("findByEmail: entity not found - Failure")
    void findByEmailWhenEntityNotFoundReturnsSuccess() {
        String AUTH_EMAIL = "wrong_email";
        Optional<UserMongoDB> user = repository.findByEmail(AUTH_EMAIL);

        Assertions.assertFalse(
                user.isPresent(),
                String.format("User with [email]: %s can not be found", AUTH_EMAIL));
    }

    @Test
    @DisplayName("findByEmail: entity found Success")
    void saveWhenAddNewEntityReturnsSuccess() {
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserMongoDB user = getMockFactory().newUserAuthEntity(AUTH_EMAIL);

        repository.save(user);
        List<UserMongoDB> users = repository.findAll();

        Assertions.assertEquals(documentSize + 1, users.size());
    }

    @Test
    @DisplayName("findAllByRole: entity found Success")
    void findAllByRolesIdWhenRoleAdminReturnsSuccess() {
        List<UserMongoDB> users = repository.findByActiveTrueAndRolesIn(Collections.singletonList(Role.ADMIN.name()));

        Assertions.assertFalse(
                users.isEmpty(),
                String.format("User list for role [%s] can not ne empty", Role.ADMIN.name()));
        Assertions.assertEquals(ADMIN_USERS, users.size());
    }
}
