package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.register.UserAuthRegister;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.mongo.UserRepositoryMongo;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    /*@Test
    @DisplayName("findByIdentifier: entity exists - Success")
    void findByIdentifierWhenUserAuthExistsReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        Authentication mockAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);

        // Set up the mocked repository
        doReturn(Optional.of(mockAuthentication)).when(repository).findByIdentifier(AUTH_IDENTIFIER);

        // Execute the service call
        AuthenticationDTO returnedAuthEntity = service.findByIdentifier(AUTH_IDENTIFIER);

        // Assert the response
        Assertions.assertNotNull(returnedAuthEntity, String.format("Authorization entity with id %s was not found", AUTH_ID));
        Assertions.assertEquals(AUTH_ID, returnedAuthEntity.getId(), String.format("Authorization id must be %s", AUTH_ID));
        Assertions.assertEquals(AUTH_IDENTIFIER, returnedAuthEntity.getIdentifier(), String.format("Authorization id must be %s", AUTH_IDENTIFIER));
    }*/

    @Test
    @DisplayName("create: entity exists already - Failure")
    void createWhenUserAuthEntityExistsReturnsAuthException() {
        // Set up the mocked repository
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ID = FAKER.internet().uuid();
        UserAuthRegister requestAuth = getMockFactory().newAuthRegisterReq(AUTH_EMAIL, AUTH_PASSWORD);
        UserMongoDB mockUserAuth = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.of(mockUserAuth)).when(repository).findByEmail(any());

        // Asserts
        Assertions.assertThrows(
                AuthException.class,
                () -> service.create(requestAuth),
                "create must throw an AuthException");
    }

    @Test
    @DisplayName("create: new entity - Success")
    void createWhenUserAuthDataIsCorrectReturnsSuccess() {
        // Set up the mocked request entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_PASSWORD = FAKER.internet().password();
        UserAuthRegister requestAuth = getMockFactory().newAuthRegisterReq(AUTH_EMAIL, AUTH_PASSWORD);
        UserMongoDB userAuthEntity = getMockFactory().newUserAuthEntity(AUTH_ID, AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByEmail(anyString());
        doReturn(userAuthEntity).when(repository).insert(any(UserMongoDB.class));

        // Execute the service call
        UserAuthDTO returnedAuthEntity = service.create(requestAuth);

        // Asserts
        Assertions.assertNotNull(returnedAuthEntity, "UserAuth entity with username " + AUTH_EMAIL + " was not created");
        Assertions.assertNotNull(returnedAuthEntity.getCreated(), "UserAuth.created can not be null");
        Assertions.assertTrue(returnedAuthEntity.getActive(), "UserAuth.active field should be true");

        verify(repository, times(1)).findByEmail(anyString());
        verify(repository, times(1)).insert(any(UserMongoDB.class));
    }

    @Test
    @DisplayName("delete: when the entity does not exists - Failure")
    void deleteWhenAuthenticationEntityFoundReturnsSuccess() {
        // Set up the mock entities
        String AUTH_ID = FAKER.internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

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
    void deleteWhenAuthenticationEntityNotFoundReturns_Success() {
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
    void findAllWhenThereAreTwoAuthenticationEntitiesReturnsSuccess() {
        // Set up mock user entities
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
    @DisplayName("Auth Service: findAll empty list - Success")
    void findAllWhenThereAreNoEntitiesReturnsSuccess() {
        // Set up the mocked repository
        doReturn(Collections.emptyList()).when(repository).findAll();

        // Execute the service call
        List<UserAuthDTO> returnedUserList = service.findAll();

        // Assertions
        Assertions.assertTrue(returnedUserList.isEmpty(), "Returned list must be empty");

        verify(repository, times(1)).findAll();
    }

    /*@Test
    @DisplayName("Auth Service: updatePassword auth component found - Success")
    void updatePasswordWhenAuthenticationFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_ID = "user_id";
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String NEW_PASSWORD = FAKER.internet().password();
        Authentication mockedAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);
        mockedAuthentication.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));


        // Set up the mocked repository
        doReturn(Optional.of(mockedAuthentication)).when(repository).findById(any());

        // Execute the service call
        AuthenticationDTO returnedAuthentication = service.updatePassword(any(), NEW_PASSWORD);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedAuthentication.getUpdated().toLocalDate(), "Updated datetime must be today");

        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Auth Service: updatePassword auth component not found - Failure")
    void updatePasswordWhenAuthenticationNotFoundReturnsResourceNotFoundException() {
        // Set up the mocked repository
        String AUTH_ID = "user_id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(any());

        // Assertions
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.updatePassword(AUTH_ID, any()),
                "updatePassword must throw an ResourceNotFoundException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);


        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Auth Service: updateIdentifier auth component found - Success")
    void updateIdentifierWhenAuthenticationFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_ID = "my_auth_id";
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String NEW_AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        Authentication mockedAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);
        mockedAuthentication.setUpdated(LocalDateTime.of(2020, 1, 31, 12, 0));


        // Set up the mocked repository
        doReturn(Optional.of(mockedAuthentication)).when(repository).findById(AUTH_ID);

        // Execute the service call
        AuthenticationDTO returnedAuthentication = service.updateIdentifier(AUTH_ID, NEW_AUTH_IDENTIFIER);

        // Assertions
        Assertions.assertNotNull(returnedAuthentication, "Returned authentication can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedAuthentication.getUpdated().toLocalDate(), "Updated datetime must be today");

        verify(repository, times(1)).findById(AUTH_ID);
    }

    @Test
    @DisplayName("Auth Service: updateIdentifier auth component not found - Failure")
    void updateIdentifierWhenAuthenticationNotFoundReturnsResourceNotFoundException() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_IDENTIFIER);

        // Assertions
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateIdentifier(AUTH_IDENTIFIER, any()),
                "updateIdentifier must throw an ResourceNotFoundException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(AUTH_IDENTIFIER);
    }*/
}
