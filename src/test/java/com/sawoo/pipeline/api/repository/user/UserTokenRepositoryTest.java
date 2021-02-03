package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.mock.UserTokenMockFactory;
import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.model.user.UserTokenType;
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

import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserTokenRepositoryTest extends BaseRepositoryTest<UserToken, UserTokenRepository, UserTokenMockFactory> {

    private static final String USER_TOKEN_JSON_DATA_FILE_NAME = "user-token-test-data.json";
    private static final String TOKEN_ID = "42762a54-62c9-11eb-ae93-0242ac130002";

    @Autowired
    public UserTokenRepositoryTest(UserTokenRepository repository, UserTokenMockFactory mockFactory) {
        super(repository, USER_TOKEN_JSON_DATA_FILE_NAME, TOKEN_ID, UserToken.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<UserToken[]> getClazz() {
        return UserToken[].class;
    }

    @Override
    protected String getComponentId(UserToken component) {
        return component.getId();
    }

    @Override
    protected UserToken getNewEntity() {
        return getMockFactory().newEntity(getMockFactory().getFAKER().internet().uuid());
    }

    @Test
    @DisplayName("findByToken: entity found - Success")
    void findByTokenWhenEntityFoundReturnsSuccess() {
        String USER_TOKEN = "f11ba388-62cc-11eb-ae93-0242ac130002";
        String USER_ID = "50e01046-62c9-11eb-ae93-0242ac130002";
        Optional<UserToken> userToken = getRepository().findByToken(USER_TOKEN);

        Assertions.assertAll("UserToken entity must match",
                () -> Assertions.assertTrue(
                        userToken.isPresent(),
                        String.format("UserToken with [token]: %s can not be null", USER_TOKEN)),
                () -> userToken.ifPresent(u -> Assertions.assertEquals(
                        USER_TOKEN,
                        u.getToken(),
                        String.format("User [token] must be %s", USER_TOKEN))),
                () -> userToken.ifPresent(u -> Assertions.assertEquals(
                        USER_ID,
                        u.getUserId(),
                        String.format("User [id] must be %s", USER_ID))));
    }

    @Test
    @DisplayName("findByToken: entity not found - Failure")
    void findByTokenWhenEntityNotFoundReturnsSuccess() {
        String USER_TOKEN = "wrong_email";
        Optional<UserToken> userToken = getRepository().findByToken(USER_TOKEN);

        Assertions.assertFalse(
                userToken.isPresent(),
                String.format("User with [token]: %s can not be found", USER_TOKEN));
    }

    @Test
    @DisplayName("findByUserId: entities found - Success")
    void findByUserIdWhenEntityListFoundReturnsSuccess() {
        String USER_ID = "6153e632-62c9-11eb-ae93-0242ac130002";
        List<UserToken> tokens = getRepository().findAllByUserId(USER_ID);
        int TOKEN_LIST_SIZE = 2;

        Assertions.assertAll(String.format("A list of tokens must be found for userId [%s]", USER_ID),
                () -> Assertions.assertFalse(
                        tokens.isEmpty(),
                        String.format("UserToken list for [userId]: %s can not be empty", USER_ID)),
                () -> Assertions.assertEquals(
                        TOKEN_LIST_SIZE,
                        tokens.size(),
                        String.format("List of tokens for userId [%s] must be [%d]", USER_ID, TOKEN_LIST_SIZE)));
    }

    @Test
    @DisplayName("findByUserId: entities list empty - Success")
    void findByUserIdWhenEntityListEmptyReturnsSuccess() {
        String USER_ID = "wrongUserId";
        List<UserToken> tokens = getRepository().findAllByUserId(USER_ID);

        Assertions.assertAll(String.format("The list of tokens must be empty for userId [%s]", USER_ID),
                () -> Assertions.assertTrue(
                        tokens.isEmpty(),
                        String.format("UserToken list for [userId]: %s must be empty", USER_ID)));
    }

    @Test
    @DisplayName("findByUserId: entities found - Success")
    void findByUserIdAndTypeWhenEntityListFoundReturnsSuccess() {
        String USER_ID = "6153e632-62c9-11eb-ae93-0242ac130002";
        List<UserToken> tokens = getRepository().findAllByUserIdAndType(USER_ID, UserTokenType.RESET_PASSWORD);
        int TOKEN_LIST_SIZE = 1;

        Assertions.assertAll(String.format("A list of tokens must be found for userId [%s] and type [%s]", USER_ID, UserTokenType.RESET_PASSWORD),
                () -> Assertions.assertFalse(
                        tokens.isEmpty(),
                        String.format("UserToken list for [userId]: %s can not be empty", USER_ID)),
                () -> Assertions.assertEquals(
                        TOKEN_LIST_SIZE,
                        tokens.size(),
                        String.format("List of tokens for userId [%s] must be [%d]", USER_ID, TOKEN_LIST_SIZE)));
    }

    @Test
    @DisplayName("findByUserId: entities list empty - Success")
    void findByUserIdAndTypeWhenEntityListEmptyReturnsSuccess() {
        String USER_ID = "6153e632-62c9-11eb-ae93-0242ac130002";
        List<UserToken> tokens = getRepository().findAllByUserIdAndType(USER_ID, UserTokenType.SESSION_REFRESH);

        Assertions.assertAll(String.format("A list of tokens must be found for userId [%s] and type [%s]", USER_ID, UserTokenType.RESET_PASSWORD),
                () -> Assertions.assertTrue(
                        tokens.isEmpty(),
                        String.format("UserToken list for [userId]: %s must be empty", USER_ID)));
    }

    @Test
    @DisplayName("createToken: entity id is null and entity is created - Success")
    void createWhenTokenIdIsNullBeforeInsertReturnsSuccess() {
        // Set up mock entities
        UserToken newToken = getMockFactory().newEntity(null);
        String token = newToken.getToken();

        // Execute the service call
        getRepository().save(newToken);
        Optional<UserToken> savedEntity = getRepository().findByToken(token);

        // Assert the response
        Assertions.assertTrue(
                savedEntity.isPresent(),
                String.format("UserToken entity with token [%s] can not be null", token));
        savedEntity.ifPresent((ut) -> Assertions.assertNotNull(ut.getId(), "User token id can not be null"));
    }
}
