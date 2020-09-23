package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserRepositoryTest {

    private static final File USER_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "user-test-data.json").toFile();
    private static final List<String> userIdList = new ArrayList<>();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private UserRepository repository;


    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        User[] userList = mapper.readValue(USER_JSON_DATA, User[].class);

        // Load each auth entity into the dataStore
        Arrays.stream(userList).forEach((user) -> {
            userIdList.add(user.getId());
            datastoreTemplate.save(user);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAllById(userIdList, User.class);
        userIdList.clear();
    }

    @Test
    void findAllWhenThereAreEntitiesFoundReturnsSuccess() {
        Iterable<User> users = repository.findAll();

        Assertions.assertEquals(
                userIdList.size(),
                (int) StreamSupport
                        .stream(users.spliterator(), false)


                        .count(),
                String.format("Should be [%d] User entities in the database", userIdList.size()));
    }

    @Test
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String USER_ID = "auth_1";
        Optional<User> entity = repository.findById(USER_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("User with [id]: %s can not be null", USER_ID));
        Assertions.assertEquals(USER_ID, entity.get().getId(), String.format("Lead [id] must be %s", USER_ID));
    }

    @Test
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        String USER_ID = "not_found";
        Optional<User> entity = repository.findById(USER_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("User with [id]: %s has to null", USER_ID));
    }

    @Test
    void findAllByRolesIdWhenRoleAdminReturnsSuccess() {
        List<User> users = repository.findAllByRoles(Role.ADMIN.name());

        Assertions.assertFalse(users.isEmpty(), String.format("User list for role [%s] can not ne empty", Role.ADMIN.name()));
    }
}
