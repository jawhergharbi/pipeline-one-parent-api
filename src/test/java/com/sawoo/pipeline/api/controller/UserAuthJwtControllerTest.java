package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.user.*;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthJwtControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthJwtService service;

    @Test
    @DisplayName("POST /api/auth/register: register user - Success")
    void registerWhenUserAndPasswordMatchesReturnsSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister = getMockFactory()
                .newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_EMAIL, Role.ADMIN.name());

        // setup the mocked service
        doReturn(mockUserAuth).when(service).create(postRegister);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is(AUTH_EMAIL)));

        ArgumentCaptor<UserAuthRegister> authRequestCaptor = ArgumentCaptor.forClass(UserAuthRegister.class);
        verify(service, times(1)).create(authRequestCaptor.capture());
        Assertions.assertEquals(AUTH_EMAIL, authRequestCaptor.getValue().getEmail());
        Assertions.assertEquals(AUTH_FULL_NAME, authRequestCaptor.getValue().getFullName());
        Assertions.assertEquals(AUTH_PASSWORD, authRequestCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("POST /api/auth/register: register user password and confirm password do not match (LAN - default) - Failure")
    void registerWhenUserPasswordAndConfirmPasswordDoNotMatchReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister = getMockFactory()
                .newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_ANOTHER_PASSWORD, AUTH_FULL_NAME);

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
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_ANOTHER_PASSWORD, AUTH_FULL_NAME);

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
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, "", AUTH_ANOTHER_PASSWORD, AUTH_FULL_NAME);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [password] in component [userAuthRegister] is bellow its min size")));
    }

    @Test
    @DisplayName("POST /api/auth/register: service returns null object - Failure")
    void registerWhenServiceReturnsNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);

        // setup the mocked service
        doReturn(null).when(service).create(any());

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. email is null - Failure")
    void registerWhenRequestBodyInvalidEmailNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(null, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);


        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [email] in component [userAuthRegister] can not be empty")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password is empty - Failure")
    void registerWhenRequestBodyInvalidPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ANOTHER_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, null, AUTH_ANOTHER_PASSWORD, AUTH_FULL_NAME);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [password] in component [userAuthRegister] can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password and ConfirmPassword are empty - Failure")
    void registerWhenRequestBodyInvalidPasswordAndConfirmPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, null, null, AUTH_FULL_NAME);


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
                                containsString("in component [userAuthRegister] can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. FullName exceeds max size - Failure")
    void registerWhenRequestBodyInvalidFullNameExceedMaxSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_FULL_NAME = FAKER.lorem().fixedString(101);
        String AUTH_PASSWORD = FAKER.internet().password();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [fullName] in component [userAuthRegister] has exceeded its max size")));
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Password is bellow min size - Failure")
    void registerWhenRequestBodyInvalidPasswordAndConfirmPasswordAreBellowMinSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password(1, 5);
        String AUTH_FULL_NAME = FAKER.name().fullName();
        UserAuthRegister postRegister =
                getMockFactory().newUserAuthRegister(AUTH_EMAIL, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        containsString("in component [userAuthRegister] is bellow its min size")));
    }

    @Test
    @DisplayName("DELETE /api/auth/logout/{identifier} logout valid request - Success")
    void logoutWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String AUTH_ID = FAKER.internet().uuid();

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/logout/{ud}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/auth/login login valid request - Success")
    void loginWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_ID = FAKER.internet().uuid();
        UserAuthLogin loginRequest = new UserAuthLogin(AUTH_EMAIL, AUTH_PASSWORD);
        UserAuthDetails mockUserDetails = getMockFactory().newUserAuthDetails(AUTH_EMAIL, AUTH_PASSWORD, AUTH_ID, Role.USER.name());

        // setup the mocked controllerHelper
        doReturn(mockUserDetails).when(service).authenticate(AUTH_EMAIL, AUTH_PASSWORD);

        // execute the POST request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login login invalid request password not informed - Failure")
    void loginWhenInvalidRequestPasswordNotInformedReturnsFailure() throws Exception {
        // Setup mock entities
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserAuthLogin loginRequest = new UserAuthLogin(AUTH_EMAIL, null);

        // execute the POST request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("DELETE /api/auth/{id}: Delete when authId is informed  - Success")
    void deleteWhenPathParameterExistsReturnSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_ID, AUTH_EMAIL, Role.SA.name());

        // setup the mocked helper
        doReturn(mockUserAuth).when(service).delete(AUTH_ID);


        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{id}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("DELETE /api/auth/{id}: Delete when authId is empty  - Failure")
    void deleteWhenPathParameterIsEmptyReturnSuccess() throws Exception {
        String AUTH_ID = "";

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{id}", AUTH_ID))
                .andExpect(status().isMethodNotAllowed());

    }

    @Test
    @DisplayName("DELETE /api/auth/{id}: Delete when authentication is not found  - Failure")
    void deleteWhenAuthenticationEntityNotFoundReturnResourceNotFoundException() throws Exception {
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", AUTH_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).delete(AUTH_ID);

        // execute the DELETE request
        mockMvc.perform(delete("/api/auth/{id}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("DELETE operation. Component type [User]")));

        // Verify behavior
        verify(service, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("GET /api/auth/{id}: Get when authentication does exist  - Failure")
    void getByIdWhenAuthenticationEntityDoesNotExistReturnResourceNotFoundException() throws Exception {
        // Setup mock authentication entity
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", AUTH_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).findById(AUTH_ID);

        // execute the GET request
        mockMvc.perform(get("/api/auth/{id}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [User]")));

        // Verify behavior
        verify(service, times(1)).findById(AUTH_ID);
    }

    @Test
    @DisplayName("GET /api/auth/{authId}: Get when authentication does exist  - Success")
    void getByIdWhenAuthenticationEntityDoesExistReturnSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_ID = FAKER.bothify(FAKER_USER_ID_REGEX);
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_ID, AUTH_EMAIL, Role.SA.name());

        // setup the mocked helper
        doReturn(mockUserAuth).when(service).findById(AUTH_ID);

        // execute the GET request
        mockMvc.perform(get("/api/auth/{id}", AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.email", is(AUTH_EMAIL)))
                .andExpect(jsonPath("$.active", is(true)));

        // Verify behavior
        verify(service, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("GET /api/auth/: Get all authentication entities  - Success")
    void getAllWhenThereAreTwoEntitiesReturnsSuccess() throws Exception {
        // Setup mock authentication entities
        String AUTH_EMAIL_1 = FAKER.internet().emailAddress();
        String AUTH_EMAIL_2 = FAKER.internet().emailAddress();
        List<UserAuthDTO> userList =
                Arrays.asList(
                        getMockFactory().newUserAuthDTO(AUTH_EMAIL_1, Role.SA.name()),
                        getMockFactory().newUserAuthDTO(AUTH_EMAIL_2, Role.CSM.name()));

        // setup the mocked helper
        doReturn(userList).when(service).findAll();

        // execute the GET request
        mockMvc.perform(get("/api/auth"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].email", is(AUTH_EMAIL_1)))
                .andExpect(jsonPath("$.[0].active", is(true)));

        // Verify behavior
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/auth: Get an empty authentication list  - Success")
    void getAllWhenNoAuthorizationEntityReturnsSuccess() throws Exception {
        // setup the mocked helper
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // execute the GET request
        mockMvc.perform(get("/api/auth"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify behavior
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Success")
    void updateWhenUpdateAuthenticationPasswordReturnsSuccess() throws Exception {
        // set up mock entities
        String AUTH_NEW_PASSWORD = FAKER.internet().password();
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserAuthUpdateDTO updateRequest = new UserAuthUpdateDTO();
        updateRequest.setPassword(AUTH_NEW_PASSWORD);
        updateRequest.setConfirmPassword(AUTH_NEW_PASSWORD);
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_ID, AUTH_EMAIL, Role.SA.name());

        // setup the mocked helper
        doReturn(mockUserAuth).when(service).update(any());

        // execute the PUT request
        mockMvc.perform(put("/api/auth/{id}", AUTH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.email", is(AUTH_EMAIL)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication fullName  - Success")
    void updateWhenUpdateAuthenticationFullNameReturnsSuccess() throws Exception {
        // set up mock entities
        String AUTH_NEW_FULL_NAME = FAKER.name().fullName();
        String AUTH_ID = FAKER.internet().uuid();
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserAuthUpdateDTO updateRequest = new UserAuthUpdateDTO();
        updateRequest.setFullName(AUTH_NEW_FULL_NAME);
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_ID, AUTH_EMAIL, Role.SA.name());
        mockUserAuth.setFullName(AUTH_NEW_FULL_NAME);

        // setup the mocked helper
        doReturn(mockUserAuth).when(service).update(any());

        // execute the PUT request
        mockMvc.perform(put("/api/auth/{id}", AUTH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(AUTH_ID)))
                .andExpect(jsonPath("$.email", is(AUTH_EMAIL)))
                .andExpect(jsonPath("$.fullName", is(AUTH_NEW_FULL_NAME)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Failure")
    void updateWhenIdPathVariableNotInformedReturnsFailure() throws Exception {
        String AUTH_ID = "";

        // execute the PUT request
        mockMvc.perform(put("/api/auth/{id}", AUTH_ID))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("GET /api/auth/role - Success")
    void findAllByRolesWhenThereAreSomeUserMatchingRolesRequestedReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        int listSize = 3;
        List<UserAuthDTO> userList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String AUTH_EMAIL = FAKER.internet().emailAddress();
                    return getMockFactory().newUserAuthDTO(AUTH_EMAIL, Role.SA.name());
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(userList).when(service).findAllByRole(any());

        // Execute the GET request
        mockMvc.perform(get("/api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .param("roles", Role.USER.name()))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].roles", containsInAnyOrder(Role.SA.name())))
                .andExpect(jsonPath("$[0].active", is(true)));
    }

    @Test
    @DisplayName("GET /api/auth/roles: role list null - Failure")
    void findAllByRolesWhenInvalidRequestRoleListNullFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/api/auth/role"))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", containsString("Missing request param : [type: String[], name: roles]")));
    }

    @Test
    @DisplayName("GET /api/auth/role: role list empty - Failure")
    void findAllByRolesWhenInvalidRequestRoleListEmptyFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/api/auth/role")
                .param("roles", ""))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath(
                        "$.message",
                        containsString("Field or param [roles] in component [UserAuthJwtController.findAllByRole] is bellow its min size")));
    }
}
