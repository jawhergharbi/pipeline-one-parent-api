package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequestBase;
import com.sawoo.pipeline.api.model.Authentication;
import com.sawoo.pipeline.api.repository.AuthRepository;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserAuthJwtServiceTest extends BaseServiceTest {

   /* @Autowired
    private UserAuthJwtService service;

    @MockBean
    private AuthRepository repository;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("findById: entity exists - Success")
    void findByIdWhenAuthenticationExistsReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        Authentication mockAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);

        // Set up the mocked repository
        doReturn(Optional.of(mockAuthentication)).when(repository).findById(AUTH_ID);

        // Execute the service call
        AuthenticationDTO returnedAuthEntity = service.findById(AUTH_ID);

        // Assert the response
        Assertions.assertNotNull(returnedAuthEntity, String.format("Authorization entity with id %s was not found", AUTH_ID));
        Assertions.assertEquals(AUTH_ID, returnedAuthEntity.getId(), String.format("Authorization id must be %s", AUTH_ID));
        Assertions.assertEquals(AUTH_IDENTIFIER, returnedAuthEntity.getIdentifier(), String.format("Authorization id must be %s", AUTH_IDENTIFIER));
    }

    @Test
    @DisplayName("findById: entity not found - Failure")
    void findByIdWhenAuthenticationNotFoundReturnsResourceNotRoundException() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_IDENTIFIER);

        // Asserts
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(AUTH_IDENTIFIER),
                "findBy must throw an ResourceNotFoundException");
    }

    @Test
    @DisplayName("findByIdentifier: entity exists - Success")
    void findByIdentifierWhenAuthenticationExistsReturnsSuccess() {
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
    }

    @Test
    @DisplayName("create: entity exists already - Failure")
    void createWhenAuthenticationEntityExistsReturnsAuthException() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase requestAuthentication = getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD);
        Authentication mockAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);

        // Set up the mocked repository
        doReturn(Optional.of(mockAuthentication)).when(repository).findByIdentifier(any());

        // Asserts
        Assertions.assertThrows(
                AuthException.class,
                () -> service.create(requestAuthentication, any()),
                "create must throw an AuthException");
    }

    @Test
    @DisplayName("create: entity exists already - Failure")
    void createWhenUserServiceThrowsExceptionReturnsEserException() {
        // Set up the mocked request entity
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        AuthJwtRegisterRequestBase requestAuthentication = getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByIdentifier(AUTH_IDENTIFIER);
        when(userService.create(any()))
                .thenThrow(new UserException(
                        ExceptionMessageConstants.USER_CREATE_USER_EXCEPTION,
                        new String[]{AUTH_IDENTIFIER}));

        // Asserts
        Assertions.assertThrows(
                AuthException.class,
                () -> service.create(requestAuthentication, AUTH_IDENTIFIER),
                "create must throw an AuthException");
    }

    @Test
    @DisplayName("create: new entity - Success")
    void createWhenAuthenticationDataIsCorrectReturnsSuccess() {
        // Set up the mocked request entity
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        AuthJwtRegisterRequestBase requestAuthentication = getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD);
        AuthenticationDTO mockAuthenticationDTO = newAuthenticationDTOMockedEntity(requestAuthentication, AUTH_IDENTIFIER);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByIdentifier(AUTH_IDENTIFIER);

        // Execute the service call
        AuthenticationDTO returnedAuthEntity = service.create(requestAuthentication, AUTH_IDENTIFIER);

        // Asserts
        Assertions.assertNotNull(returnedAuthEntity, "Authorization entity with identifier " + AUTH_IDENTIFIER + " was not created");
        Assertions.assertNotNull(returnedAuthEntity.getSignedUp(), "Authorization.signedUp can not be null");
        Assertions.assertEquals(mockAuthenticationDTO.getIdentifier(), returnedAuthEntity.getIdentifier(), "Authentication.identifier field should be equal");
        Assertions.assertEquals(mockAuthenticationDTO.getProviderType(), DomainConstants.AUTHORIZATION_PROVIDER_TYPE_EMAIL, "Authentication.providerType field should be equal");
    }

    @Test
    @DisplayName("Authentication Service: delete authentication when the entity does not exists - Failure")
    void deleteWhenAuthenticationEntityFoundReturnsSuccess() {
        // Set up the mocked repository
        String AUTH_ID = "user_id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(AUTH_ID);

        // Asserts
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(AUTH_ID),
                "delete must throw an ResourceNotFoundException");

        // Verify behaviour
        verify(repository, times(1)).findById(AUTH_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Authentication Service: delete authentication when the entity exists - Success")
    void deleteWhenAuthenticationEntityNotFoundReturns_Success() {
        // Set up the mocked repository
        String AUTH_IDENTIFIER = FAKER.internet().emailAddress();
        String AUTH_ID = "user_id";
        Authentication mockedAuthentication = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAuthentication)).when(repository).findById(any());

        // Execute the service call
        AuthenticationDTO returnedAuthEntity = service.delete(any());

        // Assertions
        Assertions.assertNotNull(returnedAuthEntity, "Authentication entity can not be null");
        Assertions.assertEquals(returnedAuthEntity.getId(), AUTH_ID, "Authentication.id have to be the same");
        Assertions.assertEquals(returnedAuthEntity.getIdentifier(), AUTH_IDENTIFIER, "Authentication.identifier have to be the same");
        Assertions.assertNotNull(returnedAuthEntity.getSignedUp(), "Authentication.signedUp can not be null");

        // Verify behaviour
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Auth Service: findAll when there are 2 auth entities - Success")
    void findAllWhenThereAreTwoAuthenticationEntitiesReturnsSuccess() {
        // Set up mock user entities
        String AUTH_ID_1 = "my_component_id1";
        String AUTH_ID_2 = "my_component_id2";
        String AUTH_IDENTIFIER_1 = FAKER.internet().emailAddress();
        String AUTH_IDENTIFIER_2 = FAKER.internet().emailAddress();
        List<Authentication> authList = Arrays
                .asList(
                        getMockFactory().newAuthenticationEntity(AUTH_ID_1, AUTH_IDENTIFIER_1),
                        getMockFactory().newAuthenticationEntity(AUTH_ID_2, AUTH_IDENTIFIER_2));

        // Set up the mocked repository
        doReturn(authList).when(repository).findAll();

        // Execute the service call
        List<AuthenticationDTO> returnedUserList = service.findAll();

        Assertions.assertFalse(returnedUserList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(2, returnedUserList.size(), "Returned list size must be 2");

        // Verify behaviour
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Auth Service: findAll empty list - Success")
    void findAllWhenThereAreNoEntitiesReturnsSuccess() {
        // Set up mock user entities
        List<Authentication> authenticationList = Collections.emptyList();

        // Set up the mocked repository
        doReturn(authenticationList).when(repository).findAll();

        // Execute the service call
        List<AuthenticationDTO> returnedUserList = service.findAll();

        // Assertions
        Assertions.assertTrue(returnedUserList.isEmpty(), "Returned list can not be empty");

        verify(repository, times(1)).findAll();
    }

    @Test
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
    }

    private AuthenticationDTO newAuthenticationDTOMockedEntity(AuthJwtRegisterRequest registerRequest, String identifier) {
        AuthenticationDTO mockAuthentication = new AuthenticationDTO();
        LocalDateTime SIGNED_UP_DATE_TIME = LocalDateTime.of(2020, Month.DECEMBER, 12, 12, 0);
        mockAuthentication.setId("user_id");
        mockAuthentication.setSignedUp(SIGNED_UP_DATE_TIME);
        mockAuthentication.setProviderType(
                registerRequest.getProviderType() != null ?
                        registerRequest.getProviderType() :
                        DomainConstants.AUTHORIZATION_PROVIDER_TYPE_EMAIL);
        mockAuthentication.setIdentifier(identifier);

        return mockAuthentication;
    }*/
}
