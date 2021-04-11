package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class UserAuthRepositoryTest extends BaseRepositoryTest<User, UserRepository, UserMockFactory> {

    private static final String TEST_JSON_DATA_FILE_NAME = "user-auth-test-data.json";
    private static final int ADMIN_USERS = 1;
    private static final String ENTITY_ID = "6027a2ff4542c0de858d2936";

    @Autowired
    public UserAuthRepositoryTest(UserRepository repository, UserMockFactory mockFactory) {
        super(repository, TEST_JSON_DATA_FILE_NAME, ENTITY_ID, User.class.getSimpleName(), mockFactory);
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
