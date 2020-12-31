package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.repository.BaseRepositoryTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthRepositoryTest extends BaseRepositoryTest<User, UserRepository, UserMockFactory> {

    private static final File AUTHENTICATION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "user-auth-test-data.json").toFile();
    private static final int ADMIN_USERS = 1;
    private static final String USER_ID = "5fa2e7c58b7a2a51f31f2bed";

    @Autowired
    public UserAuthRepositoryTest(UserRepository repository, UserMockFactory mockFactory) {
        super(repository, AUTHENTICATION_JSON_DATA, USER_ID, User.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<User[]> getClazz() {
        return User[].class;
    }

    @Override
    protected String getComponentId(User component) {
        return component.getId();
    }

    @Override
    protected User getNewEntity() {
        String USER_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        return getMockFactory().newEntity(USER_EMAIL, USER_PASSWORD);
    }

    @Test
    @DisplayName("findByEmail: entity found - Success")
    void findByEmailWhenEntityFoundReturnsSuccess() {
        String AUTH_EMAIL = "miguel@gmail.com";
        Optional<User> user = getRepository().findByEmail(AUTH_EMAIL);

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
        Optional<User> user = getRepository().findByEmail(AUTH_EMAIL);

        Assertions.assertFalse(
                user.isPresent(),
                String.format("User with [email]: %s can not be found", AUTH_EMAIL));
    }

    @Test
    @DisplayName("findAllByRole: entity found Success")
    void findAllByRolesIdWhenRoleAdminReturnsSuccess() {
        List<User> users = getRepository().findByActiveTrueAndRolesIn(Collections.singletonList(UserRole.ADMIN.name()));

        Assertions.assertFalse(
                users.isEmpty(),
                String.format("User list for role [%s] can not ne empty", UserRole.ADMIN.name()));
        Assertions.assertEquals(ADMIN_USERS, users.size());
    }
}
