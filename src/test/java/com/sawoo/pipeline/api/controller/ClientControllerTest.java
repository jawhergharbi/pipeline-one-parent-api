package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.controller.base.BaseControllerTestOld;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.service.ClientService;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ClientControllerTest extends BaseControllerTestOld {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService service;

    @Test
    @DisplayName("POST /api/clients: client create - Success")
    void createWhenClientCreatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        ClientBasicDTO mockedEntity = getMockFactory()
                .newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + CLIENT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(CLIENT_LINKED_IN_URL)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.company.name", is(CLIENT_COMPANY_NAME)))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("POST /api/clients: client create full name not informed - Failure")
    void createWhenInvalidRequestClientFullNameNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(null, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field or param [fullName] in component [clientBasicDTO] can not be empty")));
    }

    @Test
    @DisplayName("POST /api/clients: client create company values not informed - Failure")
    void createWhenInvalidRequestClientCompanyValuesNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, null, null);

        // Execute the POST request
        mockMvc.perform(post("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/clients: client create company null - Failure")
    void createWhenInvalidRequestClientCompanyNullReturnsFailure() throws Exception {
        // Setup the mocked entities
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO postEntity = getMockFactory().newClientDTO(CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, null, null);
        postEntity.setCompany(null);


        // Execute the POST request
        mockMvc.perform(post("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field or param [company] in component [clientBasicDTO] can not be null")));
    }

    @Test
    @DisplayName("GET /api/clients/: three clients found - Success")
    void findAllWhenThereAreThreeClientsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<Long> clientIds = new ArrayList<>();
        List<ClientBasicDTO> leadList = IntStream.range(0, 3)
                .mapToObj((lead) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    clientIds.add(CLIENT_ID);
                    return getMockFactory().newClientDTO(CLIENT_ID);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(leadList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(clientIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/clients/: no clients found - Success")
    void findAllWhenNoCompaniesFoundReturnsSuccess() throws Exception {
        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime} three clients found - Success")
    void findAllMainWhenThereAreThreeLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int listSize = 3;
        List<Long> clientIds = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<ClientBasicDTO> clientList = IntStream.range(0, listSize)
                .mapToObj((client) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    clientIds.add(CLIENT_ID);
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    return getMockFactory().newClientMainDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(clientList).when(service).findAllMain(now);

        // Execute the GET request
        mockMvc.perform(get("/api/clients/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id", is(clientIds.get(0).intValue())))
                .andExpect(jsonPath("$[1].id", is(clientIds.get(1).intValue())));
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime}: no clients found - Success")
    void findAllMainWhenNoClientsFoundReturnsSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAllMain(now);

        // Execute the GET request
        mockMvc.perform(get("/api/clients/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}: lead found - Success")
    void getByIdWhenClientFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO mockedDTOEntity =
                getMockFactory().newClientDTO(
                        CLIENT_ID,
                        CLIENT_FULL_NAME,
                        CLIENT_LINKED_IN_URL,
                        CLIENT_COMPANY_NAME,
                        CLIENT_COMPANY_URL);

        // Setup the mock service
        doReturn(Optional.of(mockedDTOEntity)).when(service).findById(CLIENT_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/clients/{id}", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(CLIENT_LINKED_IN_URL)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.company.name", is(CLIENT_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(CLIENT_COMPANY_URL)));
    }

    @Test
    @DisplayName("GET /api/clients/{id}: client not found - Failure")
    void getByIdWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Client", String.valueOf(CLIENT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(CLIENT_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/clients/{id}", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        Matchers.containsString(String.format("GET operation. Component type [Client] and id [%s] was not found", CLIENT_ID))));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id}: delete client found - Success")
    void deleteWhenClientFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedDTOEntity = getMockFactory().newClientDTO(
                CLIENT_ID,
                CLIENT_FULL_NAME,
                CLIENT_LINKED_IN_URL, true);

        // Setup the mock service
        doReturn(Optional.of(mockedDTOEntity)).when(service).delete(CLIENT_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/clients/{id}", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(CLIENT_LINKED_IN_URL)));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id}: client not found - Failure")
    void deleteWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).delete(CLIENT_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/clients/{id}", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        Matchers.containsString(String.format("DELETE operation. Component type [Client] and id [%s] was not found", CLIENT_ID))));
    }

    @Test
    @DisplayName("PUT /api/client/: update client - Success")
    void updateWhenClientFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        ClientBasicDTO postEntity = new ClientBasicDTO();
        String NEW_CLIENT_FULL_NAME = FAKER.name().fullName();
        postEntity.setFullName(NEW_CLIENT_FULL_NAME);

        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        ClientBasicDTO mockedEntity = getMockFactory()
                .newClientDTO(CLIENT_ID, NEW_CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);
        mockedEntity.setUpdated(now);

        // setup the mocked service
        doReturn(Optional.of(mockedEntity)).when(service).update(CLIENT_ID, postEntity);

        // Execute the PUT request
        executePutRequest(CLIENT_ID, postEntity)

                // Validate the returned fields
                .andExpect(jsonPath("$.fullName", is(NEW_CLIENT_FULL_NAME)))
                .andExpect(jsonPath("$.updated", startsWith(now.toLocalDate().toString())));
    }

    @Test
    @DisplayName("PUT /api/clients/{id}: client not found - Failure")
    void updateWhenClientNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        ClientBasicDTO postEntity = new ClientBasicDTO();
        String NEW_CLIENT_FULL_NAME = FAKER.name().fullName();
        postEntity.setFullName(NEW_CLIENT_FULL_NAME);
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);


        // setup the mocked service
        doReturn(Optional.empty()).when(service).update(CLIENT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(put("/api/clients/{id}", CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        Matchers.containsString(String.format("Component type [Client] and id [%s] was not found", CLIENT_ID))));
    }

    @Test
    @DisplayName("PUT /api/clients: update client (company fields) - Success")
    void updateWhenClientFoundAndUpdatedCompanyFieldReturnsSuccess() throws Exception {
        // Setup the mocked entities
        ClientBasicDTO postEntity = new ClientBasicDTO();
        String NEW_COMPANY_NAME = FAKER.company().name();
        String NEW_COMPANY_URL = FAKER.company().url();
        postEntity.setCompany(CompanyDTO
                .builder()
                .name(NEW_COMPANY_NAME)
                .url(NEW_COMPANY_URL)
                .build());

        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        ClientBasicDTO mockedEntity = getMockFactory().newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);
        mockedEntity.getCompany().setUrl(NEW_COMPANY_URL);
        mockedEntity.getCompany().setName(NEW_COMPANY_NAME);
        mockedEntity.setUpdated(now);

        // setup the mocked service
        doReturn(Optional.of(mockedEntity)).when(service).update(CLIENT_ID, postEntity);

        // Execute the PUT request
        executePutRequest(CLIENT_ID, postEntity)

                // Validate the returned fields
                .andExpect(jsonPath("$.company.name", is(NEW_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(NEW_COMPANY_URL)));
    }

    @Test
    @DisplayName("PUT /api/clients: update client (customer success manager field) - Success")
    void updateWhenClientFoundAndUpdatedCustomerSuccessManagerFieldReturnsSuccess() throws Exception {
        // Setup the mocked entities
        ClientBasicDTO postEntity = new ClientBasicDTO();
        UserDTOOld customerSuccessManager = new UserDTOOld();
        customerSuccessManager.setId(FAKER.regexify(FAKER_USER_ID_REGEX));
        customerSuccessManager.setFullName(FAKER.name().fullName());
        customerSuccessManager.setRoles(Set.of(new String[]{"USER", "MANAGER"}));
        customerSuccessManager.setActive(true);
        postEntity.setCustomerSuccessManager(customerSuccessManager);

        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String CLIENT_COMPANY_NAME = FAKER.company().name();
        String CLIENT_COMPANY_URL = FAKER.company().url();
        ClientBasicDTO mockedClientDTO = getMockFactory()
                .newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, CLIENT_COMPANY_NAME, CLIENT_COMPANY_URL);
        mockedClientDTO.setCustomerSuccessManager(customerSuccessManager);

        // setup the mocked service
        doReturn(Optional.of(mockedClientDTO)).when(service).update(CLIENT_ID, postEntity);

        // Execute the PUT request
        executePutRequest(CLIENT_ID, postEntity)

                // Validate the returned fields
                .andExpect(jsonPath("$.customerSuccessManager").exists())
                .andExpect(jsonPath("$.customerSuccessManager.roles", containsInAnyOrder("USER", "MANAGER")));
    }

    @Test
    @DisplayName("PUT /api/clients: update client setting customer success manager - Success")
    void updateCSMWhenClientFoundUserFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedClientDTO = getMockFactory().newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTOOld mockedCSMDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{"MANAGER"});
        mockedClientDTO.setCustomerSuccessManager(mockedCSMDTO);

        // setup the mocked service
        doReturn(Optional.of(mockedClientDTO)).when(service).updateCSM(CLIENT_ID, USER_ID);

        // Execute the PUT request
        mockMvc.perform(put("/api/clients/{id}/csm/{userId}", CLIENT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + CLIENT_ID))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.customerSuccessManager").exists())
                .andExpect(jsonPath("$.customerSuccessManager.id", is(USER_ID)))
                .andExpect(jsonPath("$.customerSuccessManager.fullName", is(USER_FULL_NAME)))
                .andExpect(jsonPath("$.salesAssistant").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/clients: update client setting customer success manager (client not found) - Failure")
    void updateCSMWhenClientNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String USER_ID = FAKER.lorem().fixedString(16);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).updateCSM(CLIENT_ID, USER_ID);

        // Execute the POST request
        mockMvc.perform(put("/api/clients/{id}/csm/{userId}", CLIENT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        Matchers.containsString(String.format("Component type [Client] and id [%s] was not found", CLIENT_ID))));
    }


    @Test
    @DisplayName("PUT /api/clients: update client setting customer success manager (user not found) - Failure")
    void updateCSMWhenClientFoundAndUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String USER_ID = FAKER.lorem().fixedString(16);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).updateCSM(CLIENT_ID, USER_ID);

        // Execute the POST request
        mockMvc.perform(put("/api/clients/{id}/csm/{userId}", CLIENT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        Matchers.containsString(String.format("Component type [User] and id [%s] was not found", USER_ID))));
    }

    @Test
    @DisplayName("PUT /api/clients: update client setting sales assistant - Success")
    void updateSAWhenClientFoundUserFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedClientDTO = getMockFactory().newClientDTO(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTOOld mockedCSMDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{"USER"});
        mockedClientDTO.setSalesAssistant(mockedCSMDTO);

        // setup the mocked service
        doReturn(Optional.of(mockedClientDTO)).when(service).updateSA(CLIENT_ID, USER_ID);

        // Execute the PUT request
        mockMvc.perform(put("/api/clients/{id}/sa/{userId}", CLIENT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + CLIENT_ID))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(CLIENT_ID.intValue())))
                .andExpect(jsonPath("$.salesAssistant").exists())
                .andExpect(jsonPath("$.salesAssistant.id", is(USER_ID)))
                .andExpect(jsonPath("$.salesAssistant.fullName", is(USER_FULL_NAME)))
                .andExpect(jsonPath("$.customerSuccessManager").doesNotExist());
    }

    private ResultActions executePutRequest(Long clientId, ClientBasicDTO postEntity) throws Exception {
        // Execute the PUT request
        return mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/" + clientId))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(clientId.intValue())));
    }
}
