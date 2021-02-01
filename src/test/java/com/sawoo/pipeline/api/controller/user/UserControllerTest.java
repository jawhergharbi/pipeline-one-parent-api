package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthLogin;
import com.sawoo.pipeline.api.dto.user.UserAuthResetPasswordRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserControllerTest extends BaseControllerTest<UserAuthDTO, User, UserAuthService, UserMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService service;

    @Autowired
    public UserControllerTest(UserMockFactory mockFactory, UserAuthService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.USER_CONTROLLER_API_BASE_URI,
                DBConstants.USER_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "email";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("email", "fullName", "roles", "created");
    }

    @Override
    protected Class<UserAuthDTO> getDTOClass() {
        return UserAuthDTO.class;
    }

    @Override
    protected void createWhenResourceAlreadyExistsReturnsFailure() {
        Assertions.assertTrue(true, "Override to avoid super class call");
    }

    @Override
    protected void createWhenResourceCreateReturnsSuccess() {
        Assertions.assertTrue(true, "Override to avoid super class call");
    }

    @Override
    protected void updateWhenResourceNotFoundReturnsResourceNotFoundException() {
        Assertions.assertTrue(true, "Override to avoid super class call");
    }

    @Test
    @DisplayName("POST /api/auth: create user - Success")
    void createWhenUserAndPasswordMatchesReturnsSuccess() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory().newDTO(
                null,
                USER_AUTH_EMAIL,
                USER_AUTH_PASSWORD,
                USER_AUTH_PASSWORD,
                USER_AUTH_FULL_NAME,
                new String[]{UserRole.ADMIN.name()});
        String USER_AUTH_ID = getMockFactory().getComponentId();
        UserAuthDTO mockedEntity = getMockFactory().newDTO(USER_AUTH_ID, postEntity);
        mockedEntity.setPassword(null);
        mockedEntity.setConfirmPassword(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is(USER_AUTH_EMAIL)));

        ArgumentCaptor<UserAuthDTO> authRequestCaptor = ArgumentCaptor.forClass(UserAuthDTO.class);
        verify(service, times(1)).create(authRequestCaptor.capture());
        Assertions.assertEquals(USER_AUTH_EMAIL, authRequestCaptor.getValue().getEmail());
        Assertions.assertEquals(USER_AUTH_FULL_NAME, authRequestCaptor.getValue().getFullName());
        Assertions.assertEquals(USER_AUTH_PASSWORD, authRequestCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("POST /api/auth: register user password and confirm password do not match (LAN - default) - Failure")
    void createWhenUserPasswordAndConfirmPasswordDoNotMatchReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_ANOTHER_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory()
                .newDTO(null, USER_AUTH_FULL_NAME, USER_AUTH_EMAIL, USER_AUTH_PASSWORD, USER_AUTH_ANOTHER_PASSWORD, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("does not match with the password in the system")));
    }

    @Test
    @DisplayName("POST /api/auth: register user password and confirm password do not match (LAN - es) - Failure")
    void createWhenUserPasswordAndConfirmPasswordDoNotMatchAndLanguageESReturnsFailure() throws Exception {
        // Setup mock authentication entity
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_ANOTHER_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory()
                .newDTO(null, USER_AUTH_FULL_NAME, USER_AUTH_EMAIL, USER_AUTH_PASSWORD, USER_AUTH_ANOTHER_PASSWORD, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "es")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.message",
                                containsString("no coincide con la contraseña almacenada en el sistema")
                        ));
    }

    @Test
    @DisplayName("POST /api/auth: register invalid request password is empty - Failure")
    void createWhenInvalidRequestPasswordEmptyReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_ANOTHER_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory()
                .newDTO(null, USER_AUTH_FULL_NAME, USER_AUTH_EMAIL, "", USER_AUTH_ANOTHER_PASSWORD, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                stringContainsInOrder("Field or param", "in component", "is bellow its min size")));
    }

    @Test
    @DisplayName("POST /api/auth: service returns null object - Failure")
    void createWhenServiceReturnsNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        UserAuthDTO postEntity = getMockFactory().newDTO(null);

        // setup the mocked service
        doReturn(null).when(service).create(any(UserAuthDTO.class));

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/auth: Invalid request body. email is null - Failure")
    void createWhenRequestBodyInvalidEmailNullReturnsFailure() throws Exception {
        // Setup mock authentication entity
        UserAuthDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setEmail(null);


        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                stringContainsInOrder("Field or param", "in component", "can not be empty")));
    }

    @Test
    @DisplayName("POST /api/auth: Invalid request body. Password is empty - Failure")
    void createWhenRequestBodyInvalidPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_ANOTHER_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postRegister = getMockFactory()
                .newDTO(null, USER_AUTH_EMAIL, null, USER_AUTH_ANOTHER_PASSWORD, USER_AUTH_FULL_NAME, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                stringContainsInOrder("Field or param", "in component", "can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth: Invalid request body. Password and ConfirmPassword are empty - Failure")
    void createWhenRequestBodyInvalidPasswordAndConfirmPasswordReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity =
                getMockFactory()
                        .newDTO(null, USER_AUTH_EMAIL, null, null, USER_AUTH_FULL_NAME, null);


        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.messages.length()", is(2)))
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                stringContainsInOrder("Field or param", "in component", "can not be null")));
    }

    @Test
    @DisplayName("POST /api/auth: Invalid request body. FullName exceeds max size - Failure")
    void createWhenRequestBodyInvalidFullNameExceedMaxSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().lorem().fixedString(101);
        UserAuthDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setFullName(USER_AUTH_FULL_NAME);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                stringContainsInOrder("Field or param", "in component","has exceeded its max size")));
    }

    @Test
    @DisplayName("POST /api/auth: Invalid request body. Password is bellow min size - Failure")
    void createWhenRequestBodyInvalidPasswordAndConfirmPasswordAreBellowMinSizeReturnsFailure() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password(1, 5);
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory()
                .newDTO(null, USER_AUTH_EMAIL, USER_AUTH_PASSWORD, USER_AUTH_PASSWORD, USER_AUTH_FULL_NAME, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder("in component", "is bellow its min size")));
    }

    @Test
    @DisplayName("DELETE /api/auth/logout/{id} logout valid request - Success")
    void logoutWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String USER_AUTH_ID = getMockFactory().getComponentId();

        // execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/logout/{id}", USER_AUTH_ID))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/auth/login login valid request - Success")
    void loginWhenRequestIsCorrectReturnsSuccess() throws Exception {
        // Setup mock entities
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_ID = getMockFactory().getComponentId();
        UserAuthLogin loginRequest = new UserAuthLogin(USER_AUTH_EMAIL, USER_AUTH_PASSWORD);
        UserAuthDetails mockUserDetails = getMockFactory()
                .newUserAuthDetails(USER_AUTH_EMAIL, USER_AUTH_PASSWORD, USER_AUTH_ID, UserRole.USER.name());

        // setup the mocked controllerHelper
        doReturn(mockUserDetails).when(service).authenticate(USER_AUTH_EMAIL, USER_AUTH_PASSWORD);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/login")
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
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        UserAuthLogin loginRequest = new UserAuthLogin(USER_AUTH_EMAIL, null);

        // execute the POST request
        mockMvc.perform(post(getResourceURI() + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("DELETE /api/auth/{id}: Delete when authId is empty  - Failure")
    void deleteWhenPathParameterIsEmptyReturnSuccess() throws Exception {
        String USER_AUTH_ID = "";

        // execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}", USER_AUTH_ID))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("GET /api/auth: Get an empty user auth list  - Success")
    void findAllWhenNoAuthorizationEntityReturnsSuccess() throws Exception {
        // setup the mocked helper
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // execute the GET request
        mockMvc.perform(get(getResourceURI()))

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
        String USER_AUTH_NEW_PASSWORD = getMockFactory().getFAKER().internet().password();
        String USER_AUTH_ID = getMockFactory().getComponentId();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        UserAuthUpdateDTO updateRequest = new UserAuthUpdateDTO();
        updateRequest.setPassword(USER_AUTH_NEW_PASSWORD);
        updateRequest.setConfirmPassword(USER_AUTH_NEW_PASSWORD);
        UserAuthDTO mockedEntity = getMockFactory()
                .newDTO(USER_AUTH_ID, USER_AUTH_EMAIL, USER_AUTH_NEW_PASSWORD, USER_AUTH_NEW_PASSWORD,  null, null);

        // setup the mocked helper
        doReturn(mockedEntity).when(service).update(any(UserAuthUpdateDTO.class));

        // execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", USER_AUTH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(USER_AUTH_ID)))
                .andExpect(jsonPath("$.email", is(USER_AUTH_EMAIL)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication fullName  - Success")
    void updateWhenUpdateAuthenticationFullNameReturnsSuccess() throws Exception {
        // set up mock entities
        String USER_AUTH_NEW_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_AUTH_ID = getMockFactory().getComponentId();
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        UserAuthUpdateDTO updateRequest = new UserAuthUpdateDTO();
        updateRequest.setFullName(USER_AUTH_NEW_FULL_NAME);
        UserAuthDTO mockedUser = getMockFactory().newDTO(USER_AUTH_ID);
        mockedUser.setFullName(USER_AUTH_NEW_FULL_NAME);
        mockedUser.setEmail(USER_AUTH_EMAIL);

        // setup the mocked helper
        doReturn(mockedUser).when(service).update(any(UserAuthUpdateDTO.class));

        // execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", USER_AUTH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(USER_AUTH_ID)))
                .andExpect(jsonPath("$.email", is(USER_AUTH_EMAIL)))
                .andExpect(jsonPath("$.fullName", is(USER_AUTH_NEW_FULL_NAME)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/auth: Put update Authentication password  - Failure")
    void updateWhenUserIdInformedReturnsFailure() throws Exception {
        String USER_AUTH_ID = "";

        // execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", USER_AUTH_ID))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("GET /api/auth/role - Success")
    void findAllByRolesWhenThereAreSomeUserMatchingRolesRequestedReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        int listSize = 3;
        List<UserAuthDTO> userList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
                    String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
                    String USER_AUTH_ID = getMockFactory().getComponentId();
                    return getMockFactory()
                            .newDTO(USER_AUTH_ID, USER_AUTH_EMAIL, USER_AUTH_PASSWORD, new String[]{UserRole.AST.name()});
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(userList).when(service).findAllByRole(any());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .param("roles", UserRole.USER.name()))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].roles", containsInAnyOrder(UserRole.AST.name())))
                .andExpect(jsonPath("$[0].active", is(true)));
    }

    @Test
    @DisplayName("GET /api/auth/roles: role list null - Failure")
    void findAllByRolesWhenInvalidRequestRoleListNullReturnsFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/role"))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", containsString("Missing request param : [type: String[], name: roles]")));
    }

    @Test
    @DisplayName("GET /api/auth/role: role list empty - Failure")
    void findAllByRolesWhenInvalidRequestRoleListEmptyReturnsFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/role")
                .param("roles", ""))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath(
                        "$.message",
                        stringContainsInOrder("Field or param", "in component", "is bellow its min size")));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password: reset password when email found - Success")
    void resetPasswordWhenEmailFoundReturnsSuccess() throws Exception {
        // Assign
        String USER_EMAIl = getMockFactory().getFAKER().internet().emailAddress();
        String USER_TOKEN_ID = getMockFactory().getFAKER().internet().uuid();
        UserTokenDTO tokenDTO = getMockFactory().getUserTokenMockFactory().newDTO(USER_TOKEN_ID);

        // setup the mocked service
        doReturn(tokenDTO).when(service).resetPassword(anyString());

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/reset-password")
                .param("email", USER_EMAIl))

                // Validate the response code and the content type
                .andExpect(status().isNoContent());

        // Verify
        verify(service, atMostOnce()).resetPassword(anyString());
    }

    @Test
    @DisplayName("POST /api/auth/reset-password: reset password when email is invalid - Failure")
    void resetPasswordWhenEmailNotValidReturnsFailure() throws Exception {
        // Assign
        String USER_EMAIl = "wrongEmail";

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/reset-password")
                .param("email", USER_EMAIl))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder("Field or param", "in component", "must be a proper email address")));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password: reset password when email is empty - Failure")
    void resetPasswordWhenEmailEmptyReturnsFailure() throws Exception {
        // Assign
        String USER_EMAIl = "";

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/reset-password")
                .param("email", USER_EMAIl))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder("Field or param", "in component", "can not be empty or null")));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password: reset password when service thrown auth exception - Failure")
    void resetPasswordWhenServiceThrowsAuthExceptionReturnsFailure() throws Exception {
        // Assign
        String USER_EMAIl = getMockFactory().getFAKER().internet().emailAddress();

        // setup the mocked service
        AuthException exception = new AuthException(
                ExceptionMessageConstants.AUTH_RESET_PASSWORD_USER_EMAIL_NOT_FOUND_ERROR_EXCEPTION,
                new String[]{USER_EMAIl});

        doThrow(exception).when(service).resetPassword(anyString());

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/reset-password")
                .param("email", USER_EMAIl))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath(
                        "$.message",
                        stringContainsInOrder("Authentication component. Reset password user not found", USER_EMAIl)));
        // Verify
        verify(service, atMostOnce()).resetPassword(anyString());
    }

    @Test
    @DisplayName("POST /api/auth/confirm-reset-password: confirm reset password when service thrown auth exception - Failure")
    void confirmResetPasswordWhenPasswordAndConfirmPasswordDoesNotMatchReturnsFailure() throws Exception {
        // Assign
        String TOKEN = UUID.randomUUID().toString();
        String PASSWORD = getMockFactory().getFAKER().internet().password(8, 12);
        String CONFIRM_PASSWORD = getMockFactory().getFAKER().internet().password(8, 12);
        UserAuthResetPasswordRequest resetPassword = UserAuthResetPasswordRequest.builder()
                .password(PASSWORD)
                .confirmPassword(CONFIRM_PASSWORD)
                .token(TOKEN)
                .build();

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/confirm-reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPassword)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath(
                        "$.message",
                        stringContainsInOrder(
                                "Authentication component. Reset password for token",
                                "password and confirmPassword does not match")));
        // Verify
        verify(service, never()).confirmResetPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/confirm-reset-password: confirm reset password when password empty - Failure")
    void confirmResetPasswordWhenPasswordEmptyReturnsFailure() throws Exception {
        // Assign
        String TOKEN = UUID.randomUUID().toString();
        String PASSWORD = Strings.EMPTY;
        String CONFIRM_PASSWORD = getMockFactory().getFAKER().internet().password(8, 12);
        UserAuthResetPasswordRequest resetPassword = UserAuthResetPasswordRequest.builder()
                .password(PASSWORD)
                .confirmPassword(CONFIRM_PASSWORD)
                .token(TOKEN)
                .build();

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/confirm-reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPassword)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder(
                                "Field or param",
                                "is bellow its min size")));
        // Verify
        verify(service, never()).confirmResetPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/confirm-reset-password: confirm reset password when token not informed - Failure")
    void confirmResetPasswordWhenTokenNullReturnsFailure() throws Exception {
        // Assign
        String PASSWORD = getMockFactory().getFAKER().internet().password(8, 12);
        String CONFIRM_PASSWORD = getMockFactory().getFAKER().internet().password(8, 12);
        UserAuthResetPasswordRequest resetPassword = UserAuthResetPasswordRequest.builder()
                .password(PASSWORD)
                .confirmPassword(CONFIRM_PASSWORD)
                .build();

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/confirm-reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPassword)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder(
                                "Field or param",
                                "can not be empty")));
        // Verify
        verify(service, never()).confirmResetPassword(anyString(), anyString());
    }
}
