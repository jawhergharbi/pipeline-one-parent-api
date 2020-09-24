package com.sawoo.pipeline.api.controller;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.login.AuthJwtLoginRequestBase;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequestBase;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdateIdentifierRequest;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdatePasswordRequest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "unit-tests")
class AuthJwtControllerBaseTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private AuthJwtControllerHelper controllerHelper;

    @Test
    @DisplayName("POST /api/auth/register: register user - Success")
    void registerWhenUserAndPasswordMatchesReturnsSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase postRegister = getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);
        AuthenticationDTO mockAuthResponse = newAuthenticationDTO(AUTH_IDENTIFIER, AUTH_ID);


        // setup the mocked controllerHelper
        doReturn(mockAuthResponse).when(controllerHelper).createAuthResponse(postRegister, AUTH_IDENTIFIER);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.signedUp", is("2020-12-12T12:00:00")))
                .andExpect(jsonPath("$.identifier", is(AUTH_IDENTIFIER)))
                .andExpect(jsonPath("$.providerType", is(0)));

        ArgumentCaptor<AuthJwtRegisterRequestBase> authRequestCaptor = ArgumentCaptor.forClass(AuthJwtRegisterRequestBase.class);
        verify(controllerHelper, times(1)).createAuthResponse(authRequestCaptor.capture(), eq(AUTH_IDENTIFIER));
        Assertions.assertEquals(AUTH_IDENTIFIER, authRequestCaptor.getValue().getIdentifier());
        Assertions.assertEquals(AUTH_FULL_NAME, authRequestCaptor.getValue().getFullName());
        Assertions.assertEquals(AUTH_PASSWORD, authRequestCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("POST /api/auth/register: register user password and confirm password do not match (LAN - default) - Failure")
    void registerWhenUserPasswordAndConfirmPasswordDoNotMatchReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = "my_identifier";
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        AuthJwtRegisterRequestBase postRegister =
                getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD, AUTH_ANOTHER_PASSWORD);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("does not match with the password in the system")));
    }

    @Test
    @DisplayName("POST /api/auth/register: register user password and confirm password do not match (LAN - es) - Failure")
    void registerWhenUserPasswordAndConfirmPasswordDoNotMatchAndLanguageESReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        AuthJwtRegisterRequestBase postRegister =
                getMockFactory().newAuthRegisterRequest(AUTH_IDENTIFIER, AUTH_PASSWORD, AUTH_ANOTHER_PASSWORD);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "es")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.message",
                                containsString("no coincide con la contraseña almacenada en el sistema")
                        ));
    }

    @Test
    @DisplayName("POST /api/auth/register: register invalid request password is empty - Failure")
    void registerWhenInvalidRequestPasswordEmptyReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = "my_identifier";
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        "",
                        "another_password",
                        Faker.instance().name().fullName(),
                        null,
                        0);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [password] in component [authJwtRegisterRequestBase] is bellow its min size")));
    }

    @Test
    @DisplayName("POST /api/auth/register: service returns null object - Failure")
    void registerWhenServiceReturnsNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        "my_password",
                        "a_different_password",
                        Faker.instance().name().fullName(),
                        null,
                        0);

        // setup the mocked helper
        doReturn(null).when(controllerHelper).createAuthResponse(any(), eq(AUTH_IDENTIFIER));

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Identifier is null - Failure")
    void registerWhenRequestBodyInvalidIdentifierNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        null,
                        "my_password",
                        "my_password",
                        Faker.instance().name().fullName(),
                        null,
                        null);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [identifier] in component [authJwtRegisterRequestBase] can not be empty")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password is empty - Failure")
    void registerWhenRequestBodyInvalidPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        null,
                        "my_password",
                        Faker.instance().name().fullName(),
                        null,
                        0);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [password] in component [authJwtRegisterRequestBase] can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password and ConfirmPassword are empty - Failure")
    void registerWhenRequestBodyInvalidPasswordAndConfirmPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        null,
                        null,
                        Faker.instance().name().fullName(),
                        null,
                        0);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.messages.length()", is(2)))
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("in component [authJwtRegisterRequestBase] can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. FullName exceeds max size - Failure")
    void registerWhenRequestBodyInvalidFullNameExceedMaxSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        FAKER.internet().password(),
                        FAKER.internet().password(),
                        Faker.instance().lorem().fixedString(101),
                        null,
                        0);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [fullName] in component [authJwtRegisterRequestBase] has exceeded its max size")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password is bellow min size - Failure")
    void registerWhenRequestBodyInvalidPasswordAndConfirmPasswordAreBellowMinSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.regexify(FAKER_USER_ID_REGEX);
        String AUTH_PASSWORD = FAKER.internet().password(1, 5);
        String AUTH_FULL_NAME = Faker.instance().name().fullName();
        AuthJwtRegisterRequestBase postRegister =
                new AuthJwtRegisterRequestBase(
                        AUTH_IDENTIFIER,
                        AUTH_PASSWORD,
                        AUTH_PASSWORD,
                        AUTH_FULL_NAME,
                        null,
                        0);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        containsString("in component [authJwtRegisterRequestBase] is bellow its min size")));
    }

    @Test
    @DisplayName("DELETE /api/auth/logout/{identifier} logout valid request - Success")
    void logoutWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String AUTH_IDENTIFIER = FAKER.name().username();

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/logout/{identifier}", AUTH_IDENTIFIER))

                // Validate the response code and content type
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> identifierCaptor = ArgumentCaptor.forClass(String.class);
        verify(controllerHelper, times(1)).invalidateToken(identifierCaptor.capture());

        Assertions.assertEquals(
                AUTH_IDENTIFIER,
                identifierCaptor.getValue(),
                String.format("Logout request identifier must be equal to [%s]", AUTH_IDENTIFIER));
    }

    @Test
    @DisplayName("POST /api/auth/login login valid request - Success")
    void loginWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_PASSWORD = FAKER.internet().password();
        AuthJwtLoginRequestBase loginRequest = new AuthJwtLoginRequestBase(AUTH_IDENTIFIER, AUTH_PASSWORD);
        String TOKEN = FAKER.internet().macAddress();

        // setup the mocked controllerHelper
        doReturn(TOKEN).when(controllerHelper).token(AUTH_IDENTIFIER, AUTH_PASSWORD);

        // execute the POST request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(TOKEN)));
    }

    @Test
    @DisplayName("POST /api/auth/login login invalid request password not informed - Failure")
    void loginWhenInvalidRequestPasswordNotInformedReturnsFailure() throws Exception {
        // Setup mock entities
        String AUTH_IDENTIFIER = FAKER.name().username();
        AuthJwtLoginRequestBase loginRequest = new AuthJwtLoginRequestBase(AUTH_IDENTIFIER, null);

        // execute the POST request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("DELETE /api/auth/{authId}: Delete when authId is informed  - Success")
    void deleteWhenPathParameterExistsReturnSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthenticationDTO mockAuthResponse = newAuthenticationDTO(AUTH_IDENTIFIER, AUTH_ID);

        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(mockAuthResponse)).when(controllerHelper).delete(AUTH_ID);


        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{authId}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.identifier", is(AUTH_IDENTIFIER)))
                .andExpect(jsonPath("$.providerType", is(DomainConstants.AUTHORIZATION_PROVIDER_TYPE_EMAIL)));
    }

    @Test
    @DisplayName("DELETE /api/auth/{authId}: Delete when authId is empty  - Failure")
    void deleteWhenPathParameterIsEmptyReturnSuccess() throws Exception {
        String AUTH_ID = "";

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{authId}", AUTH_ID))
                .andExpect(status().isMethodNotAllowed());

    }

    @Test
    @DisplayName("DELETE /api/auth/{authId}: Delete when authentication is not found  - Failure")
    void deleteWhenAuthenticationEntityNotFoundReturnResourceNotFoundException() throws Exception {
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Authentication", AUTH_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(controllerHelper).delete(AUTH_ID);

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{authId}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("DELETE operation. Component type [Authentication]")));
    }

    @Test
    @DisplayName("GET /api/auth/{authId}: Get when authentication does exist  - Success")
    void getByIdWhenAuthenticationEntityDoesExistReturnSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Authentication", AUTH_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(controllerHelper).delete(AUTH_ID);

        // execute the GET request
        mockMvc.perform(get("/api/auth/{authId}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Authentication]")));

        // Verify behavior
        verify(controllerHelper, times(1)).getById(AUTH_ID);
    }

    @Test
    @DisplayName("GET /api/auth/{authId}: Get when authentication does not exist  - Failure")
    void getByIdWhenAuthenticationEntityDoesNotExistReturnResourceNotFoundException() throws Exception {
        // Setup mock authentication entity
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthenticationDTO mockAuthResponse = newAuthenticationDTO(AUTH_IDENTIFIER, AUTH_ID);

        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(mockAuthResponse)).when(controllerHelper).getById(AUTH_ID);

        // execute the GET request
        mockMvc.perform(get("/api/auth/{authId}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(AUTH_ID)));

        // Verify behavior
        verify(controllerHelper, times(1)).getById(AUTH_ID);

    }

    @Test
    @DisplayName("GET /api/auth: Get all authentication entities  - Success")
    void getAllWhenThereAreTwoEntitiesReturnsSuccess() throws Exception {
        // Setup mock authentication entities
        String AUTH_IDENTIFIER_1 = FAKER.name().username();
        String AUTH_ID_1 = FAKER.bothify(FAKER_USER_ID_REGEX);
        String AUTH_IDENTIFIER_2 = FAKER.name().username();
        String AUTH_ID_2 = FAKER.bothify(FAKER_USER_ID_REGEX);
        List<AuthenticationDTO> authenticationList =
                Arrays.asList(
                        newAuthenticationDTO(AUTH_IDENTIFIER_1, AUTH_ID_1),
                        newAuthenticationDTO(AUTH_IDENTIFIER_2, AUTH_ID_2));

        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(authenticationList)).when(controllerHelper).getAll();

        // execute the GET request
        mockMvc.perform(get("/api/auth"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Verify behavior
        verify(controllerHelper, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /api/auth: Get an empty authentication list  - Success")
    void getAllWhenNoAuthorizationEntityReturnsSuccess() throws Exception {
        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(Collections.EMPTY_LIST)).when(controllerHelper).getAll();

        // execute the GET request
        mockMvc.perform(get("/api/auth"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify behavior
        verify(controllerHelper, times(1)).getAll();
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Success")
    void updateWhenUpdateAuthenticationPasswordReturnsSuccess() throws Exception {
        // set up mock entities
        String NEW_PASSWORD = FAKER.internet().password();
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthJwtUpdatePasswordRequest updateRequest = new AuthJwtUpdatePasswordRequest(AUTH_ID, NEW_PASSWORD);
        AuthenticationDTO mockAuthResponse = newAuthenticationDTO(AUTH_IDENTIFIER, AUTH_ID);

        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(mockAuthResponse)).when(controllerHelper).updatePassword(updateRequest);

        // execute the PUT request
        mockMvc.perform(put("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.identifier", is(AUTH_IDENTIFIER)));
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Failure")
    void updateWhenPasswordDoesNotComplyWithMinSizeReturnsFailure() throws Exception {
        // set up mock entities
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthJwtUpdatePasswordRequest updateRequest = new AuthJwtUpdatePasswordRequest(AUTH_ID, "pwd");

        // execute the PUT request
        mockMvc.perform(put("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        containsString("Field or param [password] in component [authJwtUpdatePasswordRequest] is bellow its min size")));
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Failure")
    void updateWhenPasswordNullReturnsFailure() throws Exception {
        // set up mock entities
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthJwtUpdatePasswordRequest updateRequest = new AuthJwtUpdatePasswordRequest(AUTH_ID, null);

        // execute the PUT request
        mockMvc.perform(put("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        containsString("Field or param [password] in component [authJwtUpdatePasswordRequest] can not be null")));
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication identifier  - Success")
    void updateWhenUpdateAuthenticationIdentifierReturnsSuccess() throws Exception {
        // set up mock entities
        String AUTH_IDENTIFIER = FAKER.name().username();
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthJwtUpdateIdentifierRequest updateRequest = new AuthJwtUpdateIdentifierRequest(AUTH_ID, AUTH_IDENTIFIER);
        AuthenticationDTO mockAuthResponse = newAuthenticationDTO(AUTH_IDENTIFIER, AUTH_ID);

        // setup the mocked helper
        doReturn(ResponseEntity.ok().body(mockAuthResponse))
                .when(controllerHelper)
                .updateIdentifier(updateRequest, updateRequest.getIdentifier());

        // execute the PUT request
        mockMvc.perform(put("/api/auth/identifier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.identifier", is(AUTH_IDENTIFIER)));
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Failure")
    void updateWhenInvalidRequestIdentifierNotSentReturnsFailure() throws Exception {
        // set up mock entities
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        AuthJwtUpdateIdentifierRequest updateRequest = new AuthJwtUpdateIdentifierRequest(AUTH_ID, null);

        // execute the PUT request
        mockMvc.perform(put("/api/auth/identifier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        containsString("Field or param [identifier] in component [authJwtUpdateIdentifierRequest] can not be empty")));
    }

    private AuthenticationDTO newAuthenticationDTO(String identifier, String id) {
        AuthenticationDTO mockedAuthentication = new AuthenticationDTO();
        LocalDateTime SIGNED_UP_DATE_TIME = LocalDateTime.of(2020, Month.DECEMBER, 12, 12, 0);
        mockedAuthentication.setId(id);
        mockedAuthentication.setSignedUp(SIGNED_UP_DATE_TIME);
        mockedAuthentication.setProviderType(DomainConstants.AUTHORIZATION_PROVIDER_TYPE_EMAIL);
        mockedAuthentication.setIdentifier(identifier);
        return mockedAuthentication;
    }
}
