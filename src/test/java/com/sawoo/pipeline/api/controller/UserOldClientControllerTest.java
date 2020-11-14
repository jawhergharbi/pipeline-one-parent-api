package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.controller.base.BaseControllerTestOld;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserOldClientControllerTest extends BaseControllerTestOld {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClientService service;

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
}
