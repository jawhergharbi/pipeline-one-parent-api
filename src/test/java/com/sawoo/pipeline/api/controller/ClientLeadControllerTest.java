package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.service.ClientLeadService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ClientLeadControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientLeadService service;

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create - Success")
    void createWhenLeadIsSuccessfullyCreatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        LeadBasicDTO postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, true);

        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        LeadBasicDTO mockedEntity = getMockFactory()
                .newLeadDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, postEntity.getCompany());

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(CLIENT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + CLIENT_ID + "/leads/" + LEAD_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.linkedInThread", is(LEAD_LINKED_THREAD)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create invalid request - Failure")
    void createWhenInvalidRequestLeadCompanyNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        LeadBasicDTO postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, false);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field or param [company] in component [leadBasicDTO] can not be null")));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create invalid request - Failure")
    void createWhenInvalidRequestLeadCompanyValuesNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        LeadBasicDTO postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, false);
        postEntity.setCompany(CompanyDTO.builder().build());

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath(
                        "$.messages",
                        hasItem(containsString("Field or param [company.url] in component [leadBasicDTO] can not be empty"))));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create invalid request - Failure")
    void createWhenInvalidRequestLeadMissingMultipleFieldsReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        LeadBasicDTO postEntity = new LeadBasicDTO();
        postEntity.setCompany(CompanyDTO.builder().build());

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(6)));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create but client not found - Failure")
    void createWhenClientNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        LeadBasicDTO postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, true);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(CLIENT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Client]")));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create but client not found - Failure")
    void createWhenClientFoundAndLeadFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        LeadBasicDTO postEntity = getMockFactory().newLeadDTO(null, true);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{"Lead", String.valueOf(LEAD_LINKED_IN_URL)});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(CLIENT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Entity type [Lead] with key")))
                .andExpect(jsonPath("$.message", containsString("already exits in the system")));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead create - Success")
    void addWhenLeadIsSuccessfullyCreatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        LeadBasicDTO mockedEntity = getMockFactory()
                .newLeadDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, true);

        // setup the mocked service
        doReturn(mockedEntity).when(service).add(CLIENT_ID, LEAD_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + CLIENT_ID + "/leads/" + LEAD_ID))

                // Validate the returned fields
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.linkedInThread", is(LEAD_LINKED_THREAD)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead add but client not found - Failure")
    void addWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).add(CLIENT_ID, LEAD_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Client]")));
    }

    @Test
    @DisplayName("POST /api/clients/{id}/leads/: lead add but client not found - Failure")
    void addWhenLeadNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Lead", String.valueOf(LEAD_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).add(CLIENT_ID, LEAD_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Lead]")));
    }

    @Test
    @DisplayName("GET /api/clients/{id}/leads/: get all leads  for the client - Success")
    void getClientAllWhenThereAreLeadsForTheClientReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        List<Long> leadIds = new ArrayList<>();
        int leadSize = 3;
        List<LeadBasicDTO> leadList = IntStream.range(0, leadSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    leadIds.add(LEAD_ID);
                    return getMockFactory().newLeadDTO(LEAD_ID, true);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(leadList).when(service).findAll(CLIENT_ID);

        // Execute the POST request
        mockMvc.perform(get("/api/clients/{id}/leads/", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(leadSize)))
                .andExpect(jsonPath("$[0].id", is(leadIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/clients/{id}/leads/: get all lead returns an empty list - Success")
    void getClientAllWhenThereAreNoLeadForTheClientReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).findAll(CLIENT_ID);

        // Execute the POST request
        mockMvc.perform(get("/api/clients/{id}/leads/", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/clients/{id}/leads/: get all leads but client not found - Failure")
    void getClientAllWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).findAll(CLIENT_ID);

        // Execute the POST request
        mockMvc.perform(get("/api/clients/{id}/leads/", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Client]")));
    }

    @Test
    @DisplayName("GET /api/clients/all/leads/main/{datetime} get all leads for all of the clients - Success")
    void getAllWhenClientFoundWithMultipleLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        ClientBaseDTO client = new ClientBaseDTO();
        client.setId(CLIENT_ID);
        List<Long> leadIds = new ArrayList<>();
        int leadListSize = 3;
        List<LeadMainDTO> leadList = IntStream.range(0, leadListSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    leadIds.add(LEAD_ID);
                    LeadMainDTO leadMain = getMockFactory().newLeadMainDTO(LEAD_ID, true, client);
                    leadMain.setClient(client);
                    return leadMain;
                }).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // setup the mocked service
        doReturn(leadList).when(service).findAllMain(now);

        // Execute the POST request
        mockMvc.perform(get("/api/clients/all/leads/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(leadListSize)))
                .andExpect(jsonPath("$[0].id", is(leadIds.get(0).intValue())))
                .andExpect(jsonPath("$[0].client").exists())
                .andExpect(jsonPath("$[0].client.id", is(CLIENT_ID.intValue())));
    }

    @Test
    @DisplayName("GET /api/clients/all/leads/main/{datetime} no leads- Success")
    void getAllWhenMultipleClientsFoundAndNoLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).findAllMain(now);

        // Execute the POST request
        mockMvc.perform(get("/api/clients/all/leads/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/clients/all/leads/main/{datetime} get all leads for all of the clients - Success")
    void getAllWhenInvalidRequestDatetimeNotInformedReturnsSuccess() {
        // Execute the GET request
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> mockMvc.perform(post("/api/clients/all/leads/{datetime}")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn());
    }

    @Test
    @DisplayName("GET /api/clients/{ids}/leads/main/{datetime}: get all leads  for the client - Success")
    void getClientsAllWhenThereIsOneClientWithMultipleLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        ClientBaseDTO client = new ClientBaseDTO();
        client.setId(CLIENT_ID);
        List<Long> leadIds = new ArrayList<>();
        int leadSize = 3;
        List<LeadMainDTO> leadList = IntStream.range(0, leadSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    leadIds.add(LEAD_ID);
                    return getMockFactory().newLeadMainDTO(LEAD_ID, true, client);
                }).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // setup the mocked service
        String clientIds = Stream.of(CLIENT_ID)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        doReturn(leadList).when(service).findClientsMain(Collections.singletonList(CLIENT_ID), now);

        // Execute the POST request

        mockMvc.perform(get("/api/clients/{clients}/leads/main/{datetime}", clientIds, now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(leadSize)))
                .andExpect(jsonPath("$[0].id", is(leadIds.get(0).intValue())));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id}/leads/{leadId}: delete lead from client where both are found - Success")
    void deleteWhenClientFoundAndLeadFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD = FAKER.internet().url();
        LeadBasicDTO mockedEntity = getMockFactory()
                .newLeadDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD, true);

        // setup the mocked service
        doReturn(mockedEntity).when(service).remove(CLIENT_ID, LEAD_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.linkedInThread", is(LEAD_LINKED_THREAD)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("DELETE /api/clients/{id}/leads/{leadId}: delete lead and client not found - Failure")
    void deleteWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).remove(CLIENT_ID, LEAD_ID);

        // Execute the POST request
        mockMvc.perform(delete("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response content
                .andExpect(jsonPath("$.message", containsString("DELETE operation. Component type [Client]")));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id}/leads/{leadId}: delete lead and client found and lead not found - Failure")
    void deleteWhenClientFoundAndLeadNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Lead", String.valueOf(LEAD_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).remove(CLIENT_ID, LEAD_ID);

        // Execute the POST request
        mockMvc.perform(delete("/api/clients/{id}/leads/{leadId}", CLIENT_ID, LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response content
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [Lead]")));
    }
}
