package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthServiceTest extends BaseServiceTest<UserAuthDTO, User, UserRepository, UserAuthService, UserMockFactory> {

    @MockBean
    private UserRepository repository;

    @MockBean
    private UserTokenService tokenService;

    @Autowired
    public UserAuthServiceTest(UserMockFactory mockFactory, UserAuthService service) {
        super(mockFactory, DBConstants.USER_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(User component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(UserAuthDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(User entity) {
        doReturn(Optional.of(entity)).when(repository).findByEmail(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up the mocked request entity
        String USER_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_ID = getMockFactory().getComponentId();
        String USER_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        UserAuthDTO postEntity = getMockFactory().newDTO(USER_EMAIL, USER_PASSWORD, USER_PASSWORD, null);
        User userAuthEntity = getMockFactory().newEntity(USER_ID, USER_EMAIL, USER_PASSWORD, null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByEmail(anyString());
        doReturn(userAuthEntity).when(repository).insert(any(User.class));

        // Execute the service call
        UserAuthDTO returnedAuthEntity = getService().create(postEntity);

        // Asserts
        Assertions.assertNotNull(returnedAuthEntity, String.format("UserAuth entity with email [%s]  was not created", USER_EMAIL));
        Assertions.assertNotNull(returnedAuthEntity.getCreated(), "UserAuth.created can not be null");
        Assertions.assertTrue(returnedAuthEntity.getActive(), "UserAuth.active field should be true");

        verify(repository, atMostOnce()).findByEmail(anyString());
        verify(repository, atMostOnce()).insert(any(User.class));
    }

    @Test
    @DisplayName("update: password user auth component found - Success")
    void updatePasswordWhenUserAuthFoundReturnsSuccess() {
        // Set up the mocked repository
        String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_NEW_PASSWORD = getMockFactory().getFAKER().internet().password();
        User mockedEntity = getMockFactory().newEntity(USER_AUTH_ID, USER_AUTH_EMAIL, USER_AUTH_NEW_PASSWORD, null);
        mockedEntity.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(USER_AUTH_ID);
        userUpdate.setPassword(USER_AUTH_NEW_PASSWORD);
        userUpdate.setConfirmPassword(USER_AUTH_NEW_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        UserAuthDTO returnedAuthentication = getService().update(userUpdate);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(
                LocalDate.now(ZoneOffset.UTC),
                returnedAuthentication.getUpdated().toLocalDate(),
                "Updated datetime must be today");

        verify(repository, atMostOnce()).findById(anyString());
    }

    @Test
    @DisplayName("update: password and confirmPassword do not match - Failure")
    void updatePasswordWhenPasswordAndConfirmPasswordDoNotMatchReturnsAuthException() {
        // Set up the mocked repository
        String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_NEW_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        String USER_AUTH_CONFIRM_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        User mockedEntity = getMockFactory().newEntity(USER_AUTH_ID, USER_AUTH_EMAIL, USER_AUTH_NEW_PASSWORD, null);

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(USER_AUTH_ID);
        userUpdate.setPassword(USER_AUTH_NEW_PASSWORD);
        userUpdate.setConfirmPassword(USER_AUTH_CONFIRM_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Assertions
        UserAuthService service = getService();
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update password must throw an AuthException");
        Assertions.assertEquals(
                ExceptionMessageConstants.AUTH_COMMON_PASSWORD_MATCH_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: password has not the min length - Failure")
    void updatePasswordWhenPasswordLengthIsShorterThanMinReturnsAuthException() {
        // Set up the mocked repository
        String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password(0, 5);
        User mockedEntity = getMockFactory().newEntity(USER_AUTH_ID);

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(USER_AUTH_ID);
        userUpdate.setPassword(USER_AUTH_PASSWORD);
        userUpdate.setConfirmPassword(USER_AUTH_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Assertions
        UserAuthService service = getService();
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update password must throw an AuthException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: email entity found - Success")
    void updateEmailWhenAuthenticationFoundReturnsSuccess() {
        // Set up the mocked repository
        String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(USER_AUTH_ID);
        userUpdate.setEmail(USER_AUTH_EMAIL);

        User mockedEntity = getMockFactory().newEntity(USER_AUTH_ID, USER_AUTH_EMAIL, null, null);
        mockedEntity.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(USER_AUTH_ID);

        // Execute the service call
        UserAuthDTO returnedAuthentication = getService().update(userUpdate);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(
                LocalDate.now(ZoneOffset.UTC),
                returnedAuthentication.getUpdated().toLocalDate(),
                "Updated datetime must be today");

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: user entity not found - Failure")
    void updateEmailWhenUserAuthNotFoundReturnsResourceNotFoundException() {
        // Set up the mocked repository
        String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(USER_AUTH_ID);
        userUpdate.setEmail(USER_AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(USER_AUTH_ID);

        // Assertions
        UserAuthService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(userUpdate),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: email user id is not informed - Failure")
    void updateEmailWhenUserAuthIdNotInformedReturnsAuthException() {
        // Set up the mocked repository
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setEmail(USER_AUTH_EMAIL);

        // Assertions
        UserAuthService service = getService();
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update must throw an AuthException");

        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
    }

    @Test
    @DisplayName("findAllByRole: when entities found - Success")
    void findAllByRoleWhenEntitiesFoundReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<User> userAuthList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String USER_AUTH_ID = getMockFactory().getFAKER().internet().uuid();
                    String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
                    return getMockFactory().newEntity(USER_AUTH_ID, USER_AUTH_EMAIL, null, new String[] {UserRole.AST.name()});
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(userAuthList).when(repository).findByActiveTrueAndRolesIn(anyList());

        // Execute the service
        List<UserAuthDTO> returnedUserList = getService().findAllByRole(Collections.singletonList(UserRole.AST.name()));

        Assertions.assertFalse(
                returnedUserList.isEmpty(),
                "Returned list can not be empty");
        Assertions.assertEquals(
                returnedUserList.size(),
                listSize,
                String.format("Returned list size must be %d", listSize));

        // Verify behaviour
        verify(repository, times(1)).findByActiveTrueAndRolesIn(anyList());
    }

    @Test
    @DisplayName("findAllByRole: list of roles is null - Failure")
    void findAllByRolesWhenListOfRolesIsNullReturnsConstraintViolationException() {

        // Assertions
        UserAuthService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.findAllByRole(null),
                "update must throw an ConstraintViolationException");

        // Asserts
        Assertions.assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    @DisplayName("findAllByRole: list of roles is empty - Failure")
    void findAllByRolesWhenListOfRolesIsEmptyReturnsConstraintViolationException() {
        // Assertions
        UserAuthService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.findAllByRole(Collections.emptyList()),
                "findAllByRole must throw an ConstraintViolationException");

        // Asserts
        Assertions.assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    @DisplayName("isValidToken: token not found - False")
    void isValidTokenWhenTokenNotFoundReturnsFalse() {
        // Set up mock entities
        String TOKEN = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(tokenService).findByToken(anyString());

        // Execute the service
        boolean isValid = getService().isTokenValid(TOKEN);

        Assertions.assertFalse(isValid, "Token must be invalid");
    }

    @Test
    @DisplayName("isValidToken: token found & valid - True")
    void isValidTokenWhenTokenFoundAndValidReturnsTrue() {
        // Set up mock entities
        String TOKEN = getMockFactory().getFAKER().internet().uuid();
        String TOKEN_ID = getMockFactory().getUserTokenMockFactory().getComponentId();
        UserTokenDTO token = getMockFactory().getUserTokenMockFactory().newDTO(TOKEN_ID);

        // Set up the mocked repository
        doReturn(Optional.of(token)).when(tokenService).findByToken(anyString());

        // Execute the service
        boolean isValid = getService().isTokenValid(TOKEN);

        Assertions.assertTrue(isValid, "Token must be valid");
    }

    @Test
    @DisplayName("isValidToken: token found & invalid - False")
    void isValidTokenWhenTokenFoundAndInValidReturnsFalse() {
        // Set up mock entities
        String TOKEN = getMockFactory().getFAKER().internet().uuid();
        String TOKEN_ID = getMockFactory().getUserTokenMockFactory().getComponentId();
        UserTokenDTO token = getMockFactory().getUserTokenMockFactory().newDTO(TOKEN_ID);
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));

        // Set up the mocked repository
        doReturn(Optional.of(token)).when(tokenService).findByToken(anyString());

        // Execute the service
        boolean isValid = getService().isTokenValid(TOKEN);

        Assertions.assertFalse(isValid, "Token must be invalid");
    }

    @Test
    @DisplayName("createToken: email is not found found & invalid - False")
    void createTokenWhenEmailNotFoundReturnsFailure() {
        String USER_EMAIL = getMockFactory().getFAKER().internet().emailAddress();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(getRepository()).findByEmail(USER_EMAIL);

        // Execute the service
        // Assertions
        UserAuthService service = getService();
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.createToken(USER_EMAIL, UserTokenType.RESET_PASSWORD, 500),
                "createToken must throw an AuthException");

        Assertions.assertEquals(
                ExceptionMessageConstants.AUTH_RESET_PASSWORD_USER_EMAIL_NOT_FOUND_ERROR_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
    }


    /**
     *   authenticate method is not tested
     */
}
