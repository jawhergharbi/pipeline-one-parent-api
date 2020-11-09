package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.UserRepositoryMongo;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthJwtServiceTest extends BaseServiceTest {

    @Autowired
    private UserAuthJwtService service;

    @MockBean
    private UserRepositoryMongo repository;

    @Test
    @DisplayName("findById: entity exists - Success")
    void findByEmailWhenUserAuthExistsReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserMongoDB mockUser = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.of(mockUser)).when(repository).findById(anyString());

        // Execute the service call
        UserAuthDTO returnedAuthEntity = service.findById(AUTH_ID);

        // Assert the response
        Assertions.assertNotNull(
                returnedAuthEntity,
                String.format("Authorization entity with id %s was not found", AUTH_ID));
        Assertions.assertEquals(
                AUTH_ID,
                returnedAuthEntity.getId(),
                String.format("Authorization id must be %s", AUTH_ID));
        Assertions.assertEquals(
                AUTH_EMAIL,
                returnedAuthEntity.getEmail(),
                String.format("Authorization username must be %s", AUTH_EMAIL));
    }

    @Test
    @DisplayName("findById: entity not found - Failure")
    void findByIdWhenUserAuthNotFoundReturnsResourceNotRoundException() {
        // Set up the mocked repository
        String AUTH_ID = FAKER.internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_ID);

        // Asserts
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(AUTH_ID),
                "findBy must throw an ResourceNotFoundException");
    }

    @Test
    @DisplayName("create: entity exists already - Failure")
    void createWhenUserAuthEntityExistsReturnsCommonServiceException() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ID = FAKER.internet().uuid();
        UserAuthDTO postEntity = getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD);
        UserMongoDB mockUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.of(mockUserAuth)).when(repository).findByEmail(any());

        // Asserts
        Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(postEntity),
                "create must throw an CommonServiceException");
    }

    @Test
    @DisplayName("create: new entity - Success")
    void createWhenUserAuthDataIsCorrectReturnsSuccess() {
        // Set up the mocked request entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_PASSWORD = FAKER.internet().password();
        UserAuthDTO postEntity = getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD);
        UserMongoDB userAuthEntity = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByEmail(anyString());
        doReturn(userAuthEntity).when(repository).insert(any(UserMongoDB.class));

        // Execute the service call
        UserAuthDTO returnedAuthEntity = service.create(postEntity);

        // Asserts
        Assertions.assertNotNull(returnedAuthEntity, "UserAuth entity with username " + AUTH_EMAIL + " was not created");
        Assertions.assertNotNull(returnedAuthEntity.getCreated(), "UserAuth.created can not be null");
        Assertions.assertTrue(returnedAuthEntity.getActive(), "UserAuth.active field should be true");

        verify(repository, times(1)).findByEmail(anyString());
        verify(repository, times(1)).insert(any(UserMongoDB.class));
    }

    @Test
    @DisplayName("delete: when the entity does not exists - Failure")
    void deleteWhenUserAuthEntityFoundReturnsSuccess() {
        // Set up the mock entities
        String AUTH_ID = FAKER.internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_ID);

        // Asserts
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(AUTH_ID),
                "delete must throw an ResourceNotFoundException");

        // Verify behaviour
        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("delete: authentication when the entity exists - Success")
    void deleteWhenUserAuthEntityNotFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.internet().uuid();
        UserMongoDB mockUserAuthEntity = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.of(mockUserAuthEntity)).when(repository).findById(any());

        // Execute the service call
        UserAuthDTO returnedAuthEntity = service.delete(AUTH_ID);

        // Assertions
        Assertions.assertNotNull(returnedAuthEntity, "UserAuth entity can not be null");
        Assertions.assertEquals(returnedAuthEntity.getId(), AUTH_ID, String.format("UserAuth.id value has to be %s", AUTH_ID));
        Assertions.assertEquals(returnedAuthEntity.getEmail(), AUTH_EMAIL, String.format("UserAuth.email value has to be %s", AUTH_EMAIL));
        Assertions.assertNotNull(returnedAuthEntity.getCreated(), "Authentication.signedUp can not be null");

        // Verify behaviour
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("findAll: when there are 2 auth entities - Success")
    void findAllWhenThereAreTwoUserAuthEntitiesReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<UserMongoDB> userAuthList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String AUTH_ID = FAKER.internet().uuid();
                    String AUTH_EMAIL = FAKER.internet().emailAddress();
                    return getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(userAuthList).when(repository).findAll();

        // Execute the service call
        List<UserAuthDTO> returnedUserList = service.findAll();

        Assertions.assertFalse(returnedUserList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(returnedUserList.size(), listSize, String.format("Returned list size must be %d", listSize));

        // Verify behaviour
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll: empty list - Success")
    void findAllWhenThereAreNoUserAuthEntitiesReturnsSuccess() {
        // Set up the mocked repository
        doReturn(Collections.emptyList()).when(repository).findAll();

        // Execute the service call
        List<UserAuthDTO> returnedUserList = service.findAll();

        // Assertions
        Assertions.assertTrue(returnedUserList.isEmpty(), "Returned list must be empty");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("update: password user auth component found - Success")
    void updatePasswordWhenUserAuthFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_NEW_PASSWORD = FAKER.internet().password();
        UserMongoDB mockedUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);
        mockedUserAuth.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(AUTH_ID);
        userUpdate.setPassword(AUTH_NEW_PASSWORD);
        userUpdate.setConfirmPassword(AUTH_NEW_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findById(anyString());

        // Execute the service call
        UserAuthDTO returnedAuthentication = service.update(userUpdate);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedAuthentication.getUpdated().toLocalDate(), "Updated datetime must be today");

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update password: password and confirmPassword do not match - Failure")
    void updatePasswordWhenPasswordAndConfirmPasswordDoNotMatchReturnsAuthException() {
        // Set up the mocked repository
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_NEW_PASSWORD = FAKER.internet().password();
        String AUTH_CONFIRM_PASSWORD = FAKER.internet().password();
        UserMongoDB mockedUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(AUTH_ID);
        userUpdate.setPassword(AUTH_NEW_PASSWORD);
        userUpdate.setConfirmPassword(AUTH_CONFIRM_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findById(anyString());

        // Assertions
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update password must throw an AuthException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.AUTH_COMMON_PASSWORD_MATCH_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);


        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("update password: password and confirmPassword do not match - Failure")
    void updatePasswordWhenPasswordLengthIsShorterThanMinReturnsAuthException() {
        // Set up the mocked repository
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_NEW_PASSWORD = FAKER.internet().password(0, 5);
        UserMongoDB mockedUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(AUTH_ID);
        userUpdate.setPassword(AUTH_NEW_PASSWORD);
        userUpdate.setConfirmPassword(AUTH_NEW_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findById(anyString());

        // Assertions
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update password must throw an AuthException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR);
        Assertions.assertEquals(2, exception.getArgs().length);


        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("update email: user auth component found - Success")
    void updateEmailWhenAuthenticationFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.internet().uuid();

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(AUTH_ID);
        userUpdate.setEmail(AUTH_EMAIL);

        UserMongoDB mockedUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);
        mockedUserAuth.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));


        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findById(AUTH_ID);

        // Execute the service call
        UserAuthDTO returnedAuthentication = service.update(userUpdate);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedAuthentication.getUpdated().toLocalDate(), "Updated datetime must be today");

        verify(repository, times(1)).findById(AUTH_ID);
    }

    @Test
    @DisplayName("update user: user auth component not found - Failure")
    void updateEmailWhenUserAuthNotFoundReturnsResourceNotFoundException() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.internet().uuid();

        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setId(AUTH_ID);
        userUpdate.setEmail(AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_ID);

        // Assertions
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(userUpdate),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(AUTH_ID);
    }

    @Test
    @DisplayName("update email: user id is not informed - Failure")
    void updateEmailWhenUserAuthIdNotInformedReturnsAuthException() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserAuthUpdateDTO userUpdate = new UserAuthUpdateDTO();
        userUpdate.setEmail(AUTH_EMAIL);

        // Assertions
        AuthException exception = Assertions.assertThrows(
                AuthException.class,
                () -> service.update(userUpdate),
                "update must throw an AuthException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR);
        Assertions.assertEquals(2, exception.getArgs().length);
    }

    @Test
    @DisplayName("findAllByRole: when there are 2 user auth entities - Success")
    void findAllByRolesWhenThereAreUserAuthEntitiesReturnsSuccess() {
        // Set up mock entities
        int listSize = 2;
        List<UserMongoDB> userAuthList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String AUTH_ID = FAKER.internet().uuid();
                    String AUTH_EMAIL = FAKER.internet().emailAddress();
                    return getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL, new String[] {Role.SA.name()});
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(userAuthList).when(repository).findByActiveTrueAndRolesIn(anyList());

        // Execute the service call
        List<UserAuthDTO> returnedUserList = service.findAllByRole(Collections.singletonList(Role.SA.name()));

        Assertions.assertFalse(returnedUserList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(returnedUserList.size(), listSize, String.format("Returned list size must be %d", listSize));

        // Verify behaviour
        verify(repository, times(1)).findByActiveTrueAndRolesIn(anyList());
    }

    @Test
    @DisplayName("findAllByRole: list of roles is null - Failure")
    void findAllByRolesWhenListOfRolesIsNullReturnsConstraintViolationException() {

        // Assertions
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
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.findAllByRole(Collections.emptyList()),
                "update must throw an ConstraintViolationException");

        // Asserts
        Assertions.assertEquals(1, exception.getConstraintViolations().size());
    }


    /**
     *   authenticate method is not tested
     */
}
