package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.base.BaseControllerTestOld;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.service.LeadServiceOld;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class LeadControllerTestOld extends BaseControllerTestOld {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadServiceOld service;

    @Test
    @DisplayName("GET /api/leads/{id}: lead found - Success")
    void getByIdWhenLeadFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
        LeadDTOOld mockedDTOEntity =
                newMockedDTO(
                        LEAD_ID,
                        LEAD_FULL_NAME,
                        LEAD_LINKED_IN_URL,
                        LEAD_LINKED_IN_THREAD_URL);

        // Setup the mock service
        doReturn(mockedDTOEntity).when(service).findById(LEAD_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.company").exists());
    }

    @Test
    @DisplayName("GET /api/leads/{id}: lead not found - Failure")
    void getByIdWhenLeadNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Lead", String.valueOf(LEAD_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(LEAD_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("GET operation. Component type [Lead] and id [%s] was not found", LEAD_ID))));
    }

    @Test
    @DisplayName("GET /api/leads/: three leads found - Success")
    void findAllWhenThereAreThreeLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<Long> leadIds = new ArrayList<>();
        List<LeadDTOOld> leadList = IntStream.range(0, 3)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    leadIds.add(LEAD_ID);
                    String LEAD_FULL_NAME = FAKER.name().fullName();
                    String LEAD_LINKED_IN_URL = FAKER.internet().url();
                    String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
                    return newMockedDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_THREAD_URL);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(leadList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(leadIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/leads/: no leads found - Success")
    void findAllWhenNoLeadsFoundReturnsSuccess() throws Exception {

        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime} three leads found - Success")
    void findAllMainWhenThereAreThreeLeadsReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int listSize = 3;
        List<Long> leadIds = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<LeadMainDTO> leadList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    leadIds.add(LEAD_ID);
                    String LEAD_FULL_NAME = FAKER.name().fullName();
                    String LEAD_LINKED_IN_URL = FAKER.internet().url();
                    String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
                    return newMockedMainDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_THREAD_URL);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(leadList).when(service).findAllMain(now);

        // Execute the GET request
        mockMvc.perform(get("/api/leads/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id", is(leadIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime}: no leads found - Success")
    void findAllMainWhenNoLeadsFoundReturnsSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAllMain(now);

        // Execute the GET request
        mockMvc.perform(get("/api/leads/main/{datetime}", now)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime}: datetime not informed - Failure")
    void findAllMainWhenRequestIsMissingPathVariableReturnsFailure() {

        // Execute the GET request
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    mockMvc.perform(post("/api/leads/{datetime}", null)
                            .contentType(MediaType.APPLICATION_JSON)).andReturn();
                });
    }

    @Test
    @DisplayName("GET /api/leads/main/{datetime}: datetime not properly formatted - Failure")
    void findAllMainWhenRequestPathVariableDatetimeIncorrectlyFormattedReturnsFailure() throws Exception {

        mockMvc.perform(post("/api/leads/{datetime}", "not a date")
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}: delete lead found - Success")
    void deleteWhenLeadFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
        LeadDTOOld mockedDTOEntity =
                newMockedDTO(
                        LEAD_ID,
                        LEAD_FULL_NAME,
                        LEAD_LINKED_IN_URL,
                        LEAD_LINKED_IN_THREAD_URL);

        // Setup the mock service
        doReturn(Optional.of(mockedDTOEntity)).when(service).delete(LEAD_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.linkedInThread", is(LEAD_LINKED_IN_THREAD_URL)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}: lead not found - Failure")
    void deleteWhenLeadNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).delete(LEAD_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("DELETE operation. Component type [Lead] and id [%s] was not found", LEAD_ID))));
    }

    @Test
    @DisplayName("POST /api/leads: lead create - Success")
    void createWhenLeadCreateReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LeadDTOOld postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        LeadDTOOld mockedEntity = newMockedDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity, LeadTypeRequestParam.LEAD.getType());

        // Execute the POST request
        mockMvc.perform(post("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/leads/" + LEAD_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.linkedInUrl", is(LEAD_LINKED_IN_URL)))
                .andExpect(jsonPath("$.company").exists())
                .andExpect(jsonPath("$.company.name").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists());
    }

    @Test
    @DisplayName("POST /api/leads: lead already exists - Failure")
    void create_when_company_already_exists_returns_failure() throws Exception {
        // Setup the mocked entities
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LeadDTOOld postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{"Lead", LEAD_FULL_NAME});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(postEntity, LeadTypeRequestParam.LEAD.getType());

        // Execute the POST request
        mockMvc.perform(post("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Entity type [Lead] with key [%s] already exits in the system", LEAD_FULL_NAME))));
    }

    @Test
    @DisplayName("POST /api/leads: lead create invalid request (linkedInUrl and linkedInThread are null) - Failure")
    void createWhenInvalidRequestReturnsFailure() throws Exception {
        // Setup the mocked entities
        String LEAD_FULL_NAME = FAKER.name().fullName();
        LeadDTOOld postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, null, null, true);

        // Execute the POST request
        mockMvc.perform(post("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/leads: lead create invalid request (company name is null) - Failure")
    void createWhenLeadCreateInvalidRequestCompanyEntryReturnsFailure() throws Exception {
        // Setup the mocked entities
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LeadDTOOld postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);
        postEntity.getCompany().setName(null);


        // Execute the POST request
        mockMvc.perform(post("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/leads: lead create invalid request (company entry null) - Failure")
    void createWhenInvalidRequestCompanyEntryNullReturnFailure() throws Exception {
        // Setup the mocked entities
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LeadDTOOld postEntity = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);

        // Execute the POST request
        mockMvc.perform(post("/api/leads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("PUT /api/leads: update lead - Success")
    void updateWhenLeadFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        LeadDTOOld postEntity = new LeadDTOOld();
        String NEW_LEAD_FULL_NAME = FAKER.name().fullName();
        postEntity.setFullName(NEW_LEAD_FULL_NAME);

        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadDTOOld mockedEntity = newMockedDTO(LEAD_ID, NEW_LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL);
        mockedEntity.setUpdated(now);

        // setup the mocked service
        doReturn(Optional.of(mockedEntity)).when(service).update(LEAD_ID, postEntity);

        // Execute the PUT request
        mockMvc.perform(put("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/leads/" + LEAD_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.fullName", is(NEW_LEAD_FULL_NAME)))
                .andExpect(jsonPath("$.updated", startsWith(now.toLocalDate().toString())));
    }

    @Test
    @DisplayName("PUT /api/leads/{id}: lead not found - Failure")
    void updateWhenLeadNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        LeadDTOOld postEntity = new LeadDTOOld();
        String NEW_LEAD_FULL_NAME = FAKER.name().fullName();
        postEntity.setFullName(NEW_LEAD_FULL_NAME);
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);


        // setup the mocked service
        doReturn(Optional.empty()).when(service).update(LEAD_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(put("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Component type [Lead] and id [%s] was not found", LEAD_ID))));
    }

    @Test
    @DisplayName("PUT /api/leads: update lead (company fields) - Success")
    void updateWhenLeadFoundAndUpdatedCompanyFieldReturnsSuccess() throws Exception {
        // Setup the mocked entities
        LeadDTOOld postEntity = new LeadDTOOld();
        String NEW_COMPANY_NAME = FAKER.company().name();
        String NEW_COMPANY_URL = FAKER.company().url();
        postEntity.setCompany(CompanyDTO
                .builder()
                .name(NEW_COMPANY_NAME)
                .url(NEW_COMPANY_URL)
                .build());

        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadDTOOld mockedEntity = newMockedDTO(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL);
        mockedEntity.getCompany().setUrl(NEW_COMPANY_URL);
        mockedEntity.getCompany().setName(NEW_COMPANY_NAME);
        mockedEntity.setUpdated(now);

        // setup the mocked service
        doReturn(Optional.of(mockedEntity)).when(service).update(LEAD_ID, postEntity);

        // Execute the PUT request
        mockMvc.perform(put("/api/leads/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/leads/" + LEAD_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID.intValue())))
                .andExpect(jsonPath("$.company.name", is(NEW_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(NEW_COMPANY_URL)));
    }

    private LeadDTOOld newMockedDTO(Long id, String fullName, String linkedInUrl, String linkedInThread) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        initMockedDTO(id, fullName, linkedInUrl, linkedInThread, mockEntityDTO);
        return mockEntityDTO;
    }

    private LeadMainDTO newMockedMainDTO(Long id, String fullName, String linkedInUrl, String linkedInThread) {
        LeadMainDTO mockEntityDTO = new LeadMainDTO();
        initMockedDTO(id, fullName, linkedInUrl, linkedInThread, mockEntityDTO);
        return mockEntityDTO;
    }

    private <M extends LeadDTOOld> void initMockedDTO(Long id, String fullName, String linkedInUrl, String linkedInThread, M mock) {
        LocalDateTime dateTime = LocalDateTime.of(2020, 12, 1, 1, 30);
        mock.setId(id);
        mock.setFullName(fullName);
        mock.setLinkedInUrl(linkedInUrl);
        mock.setLinkedInThread(linkedInThread);
        mock.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mock.setEmail(FAKER.internet().emailAddress());
        mock.setPosition(FAKER.company().profession());
        mock.setCompany(
                CompanyDTO.builder()
                        .id(FAKER.internet().uuid())
                        .name(FAKER.company().name())
                        .url(FAKER.company().url())
                        .build());
        mock.setUpdated(dateTime);
        mock.setCreated(dateTime);
    }
}
