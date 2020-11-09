package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.model.UserMongoDB;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthRepositoryTest extends BaseRepositoryTest<UserMongoDB, UserRepositoryMongo> {

    private static final File AUTHENTICATION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "user-auth-test-data.json").toFile();
    private static final int ADMIN_USERS = 1;

    @Autowired
    public UserAuthRepositoryTest(UserRepositoryMongo repository) {
        super(repository, AUTHENTICATION_JSON_DATA);
    }

    @Override
    protected Class<UserMongoDB[]> getClazz() {
        return UserMongoDB[].class;
    }

    @Test
    @DisplayName("findAll: return the entities defined in the file - Success")
    void findAllReturnsSuccess() {
        List<UserMongoDB> users = getRepository().findAll();
        Assertions.assertEquals(getDocumentSize(), users.size());
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String AUTH_ID = "5fa2e7c58b7a2a51f31f2bed";
        Optional<UserMongoDB> user = getRepository().findById(AUTH_ID);

        Assertions.assertTrue(user.isPresent(), String.format("User with [id]: %s can not be null", AUTH_ID));
        Assertions.assertEquals(AUTH_ID, user.get().getId(), String.format("User [id] must be %s", AUTH_ID));
    }

    @Test
    @DisplayName("findById: entity not found -  Failure")
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        String AUTH_ID = "wrong_id";
        Optional<UserMongoDB> user = getRepository().findById(AUTH_ID);

        Assertions.assertFalse(user.isPresent(), String.format("User with [id]: %s can be found", AUTH_ID));
    }

    @Test
    @DisplayName("findByEmail: entity found - Success")
    void findByEmailWhenEntityFoundReturnsSuccess() {
        String AUTH_EMAIL = "miguel@gmail.com";
        Optional<UserMongoDB> user = getRepository().findByEmail(AUTH_EMAIL);

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
        Optional<UserMongoDB> user = getRepository().findByEmail(AUTH_EMAIL);

        Assertions.assertFalse(
                user.isPresent(),
                String.format("User with [email]: %s can not be found", AUTH_EMAIL));
    }

    @Test
    @DisplayName("save: entity saved - Success")
    void saveWhenAddNewEntityReturnsSuccess() {
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserMongoDB user = getMockFactory().newUserAuthEntity(AUTH_EMAIL);

        getRepository().save(user);
        List<UserMongoDB> users = getRepository().findAll();

        Assertions.assertEquals(getDocumentSize() + 1, users.size());
    }

    @Test
    @DisplayName("findAllByRole: entity found Success")
    void findAllByRolesIdWhenRoleAdminReturnsSuccess() {
        List<UserMongoDB> users = getRepository().findByActiveTrueAndRolesIn(Collections.singletonList(Role.ADMIN.name()));

        Assertions.assertFalse(
                users.isEmpty(),
                String.format("User list for role [%s] can not ne empty", Role.ADMIN.name()));
        Assertions.assertEquals(ADMIN_USERS, users.size());
    }
}
