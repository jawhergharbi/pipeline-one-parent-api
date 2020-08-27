package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.service.UserClientService;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserClientControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClientService service;

    @Test
    @DisplayName("POST /api/user/{id}/clients: client create - Success")
    void createWhenUserFoundClientCreatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        Long CLIENT_ID = FAKER.number().randomNumber();
        ClientBasicDTO mockedEntity = getMockFactory()
                .newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        String USER_ID = FAKER.name().username();

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(USER_ID, postEntity);

        // Execute the GET request
        mockMvc.perform(post("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/users/" + USER_ID + "/clients/" + CLIENT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.company.name", is(CLIENT_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(CLIENT_COMPANY_URL)));
    }

    @Test
    @DisplayName("POST /api/user/{id}/clients: client create with invalid request (fullName not informed) - Failure")
    void createWhenUserFoundInvalidRequestFullNameNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, null, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        String USER_ID = FAKER.name().username();

        // Execute the GET request
        mockMvc.perform(post("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field or param [linkedInUrl] in component [clientBasicDTO] can not be empty")));
    }

    @Test
    @DisplayName("POST /api/user/{id}/clients: client create when user found and client already exists- Failure")
    void createWhenUserFoundAndClientAlreadyExistsReturnsFailure() throws Exception {
        String USER_ID = FAKER.name().username();
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_LINKED_IN_URL)});

        // setup the mocked service
        doThrow(exception).when(service).create(USER_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Entity type [Client] with key")))
                .andExpect(jsonPath("$.message", containsString("already exits in the system")));
    }

    @Test
    @DisplayName("POST /api/user/{id}/clients/{clientId}: client create - Success")
    void addWhenUserFoundClientFoundCreatedReturnsSuccess() throws Exception {
        String USER_ID = FAKER.name().username();
        Long CLIENT_ID = FAKER.number().randomNumber();
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedEntity = getMockFactory()
                .newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // setup the mocked service
        doReturn(mockedEntity).when(service).add(USER_ID, CLIENT_ID);

        // Execute the GET request
        mockMvc.perform(post("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/users/" + USER_ID + "/clients/" + CLIENT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.company").exists());
    }

    @Test
    @DisplayName("POST /api/user/{id}/clients/{clientId}: client create - Failure")
    void addWhenUserFoundClientNotFoundReturnsFailure() throws Exception {
        String USER_ID = FAKER.name().username();
        Long CLIENT_ID = FAKER.number().randomNumber();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception).when(service).add(USER_ID, CLIENT_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Client]")));
    }

    @Test
    @DisplayName("POST /api/user/{id}/clients/{clientId}: client create - Failure")
    void addWhenUserNotFoundReturnsFailure() throws Exception {
        String USER_ID = FAKER.name().username();
        Long CLIENT_ID = FAKER.number().randomNumber();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception).when(service).add(USER_ID, CLIENT_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [User]")));
    }

    @Test
    @DisplayName("GET /api/users/{id}/clients: get all clients  for the user - Success")
    void findAllWhenThereAreClientsForTheUserReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String USER_ID = FAKER.name().username();
        int listSize = 3;
        List<Long> clientIds = new ArrayList<>();
        List<ClientBasicDTO> clientList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    clientIds.add(CLIENT_ID);
                    return getMockFactory().newClientDTO(CLIENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(clientList).when(service).findAll(USER_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id", is(clientIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/users/{id}/clients: get all clients return empty list - Success")
    void findAllWhenThereAreNoClientsForTheUserReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String USER_ID = FAKER.name().username();

        // setup the mocked service
        doReturn(Collections.EMPTY_LIST).when(service).findAll(USER_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/users/{id}/clients: get all clients when user not found - Success")
    void findAllWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String USER_ID = FAKER.name().username();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception).when(service).findAll(USER_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/users/{id}/clients", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [User]")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id}/clients/{clientId}: delete client from user where both are found - Success")
    void deleteWhenUserFoundAndClientFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String USER_ID = FAKER.name().username();

        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedEntity = getMockFactory()
                .newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // setup the mocked service
        doReturn(mockedEntity).when(service).remove(USER_ID, CLIENT_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID))

                // Validate the response code and the content type
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(CLIENT_LINKED_IN_URL)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("DELETE /api/users/{id}/clients/{clientId}: delete client from user and client not found - Success")
    void deleteWhenUserFoundAndClientNotFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String USER_ID = FAKER.name().username();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception).when(service).remove(USER_ID, CLIENT_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("DELETE operation. Component type [Client]")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id}/clients/{clientId}: delete client from user and user not found - Success")
    void deleteWhenUserNotFoundFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String USER_ID = FAKER.name().username();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception).when(service).remove(USER_ID, CLIENT_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/users/{id}/clients/{clientId}", USER_ID, CLIENT_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("DELETE operation. Component type [User]")));
    }
}
