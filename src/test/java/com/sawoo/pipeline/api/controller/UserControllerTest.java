package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    @DisplayName("GET /api/users/{userId}: user found - Success")
    void getByIdWhenUserFoundReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.name().username();
        String FULL_NAME = FAKER.name().fullName();
        UserDTO mockUser = getMockFactory().newUserDTO(USER_ID, FULL_NAME);

        // Setup the mock service
        doReturn(mockUser).when(service).findById(USER_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(USER_ID)))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.fullName", is(FULL_NAME)))
                .andExpect(jsonPath("$.roles", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/users/{id}: user not found - Failure")
    void getByIdWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.name().username();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", USER_ID});

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(USER_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message",
                        containsString(String.format("GET operation. Component type [User] and id [%s] was not found", USER_ID))));
    }

    @Test
    @DisplayName("GET /api/users: two users found - Success")
    void findAllWhenThereAreUsersReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        int listSize = 2;
        List<String> idsList = new ArrayList<>();
        List<UserDTO> userList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String USER_ID = FAKER.name().username();
                    idsList.add(USER_ID);
                    return getMockFactory().newUserDTO(USER_ID);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(userList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/users/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].id", is(idsList.get(0))));

    }

    @Test
    @DisplayName("GET /api/users/: no users found - Success")
    void findAllWhenNoUsersFoundReturnsSuccess() throws Exception {

        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/users/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE /api/users/{userId}: deletes user found - Success")
    void deleteWhenUserFoundReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        String FULL_NAME = FAKER.name().fullName();
        UserDTO mockUser = getMockFactory().newUserDTO(USER_ID, FULL_NAME);

        // Setup the mock service
        doReturn(Optional.of(mockUser)).when(service).delete(USER_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(USER_ID)))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.fullName", is(mockUser.getFullName())))
                .andExpect(jsonPath("$.roles", hasSize(1)));
    }

    @Test
    @DisplayName("DELETE /api/users/{id}: user not found - Failure")
    void deleteWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.regexify("[a-z1-9]{10}");

        // setup the mocked service
        doReturn(Optional.empty()).when(service).delete(USER_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("DELETE operation. Component type [User] and id [%s] was not found", USER_ID))));
    }

    @Test
    @DisplayName("POST /api/users: user create - Success")
    void createWhenUserCreateReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        String FULL_NAME = FAKER.name().fullName();
        UserDTO postUser = new UserDTO();
        postUser.setId(USER_ID);
        postUser.setFullName(FULL_NAME);
        UserDTO mockedUser = getMockFactory().newUserDTO(USER_ID, FULL_NAME);

        // setup the mocked service
        doReturn(mockedUser).when(service).create(postUser);

        // Execute the POST request
        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postUser)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/users/" + USER_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(USER_ID)))
                .andExpect(jsonPath("$.fullName", is(FULL_NAME)));
    }

    @Test
    @DisplayName("POST /api/users: user already exists - Failure")
    void createWhenUserAlreadyExistsReturnsFailure() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        String FULL_NAME = FAKER.name().fullName();
        UserDTO postUser = new UserDTO();
        postUser.setId(USER_ID);
        postUser.setFullName(FULL_NAME);

        UserException exception = new UserException(
                ExceptionMessageConstants.USER_CREATE_USER_ALREADY_EXISTS_EXCEPTION,
                new String[]{USER_ID});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(postUser);

        // Execute the POST request
        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postUser)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", containsString(String.format("User with id [%s] already exists", USER_ID))));
    }

    @Test
    @DisplayName("POST /api/users: id not informed - Failure")
    void createWhenIdNotInformedReturnsFailure() throws Exception {
        // Setup the mocked User entities
        String FULL_NAME = FAKER.name().fullName();
        UserDTO postUser = new UserDTO();
        postUser.setFullName(FULL_NAME);

        // Execute the POST request
        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postUser)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("PUT /api/users/{id}: user exists - Success")
    void updateWhenUserFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        String FULL_NAME = FAKER.name().fullName();
        String USER_ID = FAKER.name().username();
        UserDTO postUser = getMockFactory().newUserDTO(USER_ID, FULL_NAME);
        postUser.setActive(false);
        postUser.setRoles(new HashSet<>(Collections.singletonList(Role.CSM.name())));
        UserDTO mockUser = getMockFactory().newUserDTO(USER_ID, FULL_NAME);
        mockUser.setActive(false);

        // setup the mocked service
        doReturn(Optional.of(mockUser)).when(service).update(USER_ID, postUser);

        // Execute the PUT request
        mockMvc.perform(put("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postUser)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/users/" + USER_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(USER_ID)))
                .andExpect(jsonPath("$.fullName", is(FULL_NAME)));
    }

    @Test
    @DisplayName("PUT /api/users/{id}: user not found - Success")
    void updateWhenUserNotFoundReturnsFailure() throws Exception {
        // Setup the mocked User entities
        String USER_ID = FAKER.name().username();
        UserDTO postUser = new UserDTO();
        postUser.setActive(false);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).update(USER_ID, postUser);

        // Execute the POST request
        mockMvc.perform(put("/api/users/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postUser)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Component type [User] and id [%s] was not found", USER_ID))));
    }

    @Test
    @DisplayName("GET /api/users/roles - Success")
    void findAllByRolesWhenThereAreSomeUserMatchingRolesRequestedReturnsSuccess() throws Exception {
        // Setup the mocked User entities
        int listSize = 3;
        List<UserDTO> userList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String USER_ID = FAKER.name().username();
                    String USER_FULL_NAME = FAKER.name().fullName();
                    return getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.USER.name()});
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(userList).when(service).findAllByRoles(any());

        // Execute the GET request
        mockMvc.perform(get("/api/users/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .param("roles", Role.USER.name()))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].roles", containsInAnyOrder(Role.USER.name())));
    }

    @Test
    @DisplayName("GET /api/users/roles: role list null - Failure")
    void findAllByRolesWhenInvalidRequestRoleListNullFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/api/users/roles"))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", containsString("Missing request param : [type: String[], name: roles]")));
    }

    @Test
    @DisplayName("GET /api/users/roles: role list empty - Failure")
    void findAllByRolesWhenInvalidRequestRoleListEmptyFailure() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/api/users/roles")
                .param("roles", ""))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath(
                        "$.message",
                        containsString("Field or param [roles] in component [UserController.findAllByRole] is bellow its min size")));
    }
}
