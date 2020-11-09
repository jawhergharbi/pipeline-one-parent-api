package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.service.company.CompanyService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class CompanyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService service;

    @Test
    @DisplayName("GET /api/companies/{id}: resource found - Success")
    void getByIdWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedDTOEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Setup the mock service
        doReturn(mockedDTOEntity).when(service).findById(COMPANY_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }

    @Test
    @DisplayName("GET /api/companies/{id}: resource not found - Failure")
    void getByIdWhenResourceNotFoundReturnsResourceNoFoundException() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Company", String.valueOf(COMPANY_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(COMPANY_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("GET operation. Component type [Company] and id [%s] was not found", COMPANY_ID))));
    }

    @Test
    @DisplayName("GET /api/companies/: resources found - Success")
    void findAllWhenResourcesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<String> companyIds = new ArrayList<>();
        List<CompanyDTO> companyList = IntStream.range(0, 2)
                .mapToObj((company) -> {
                    String COMPANY_ID = FAKER.internet().uuid();
                    companyIds.add(COMPANY_ID);
                    String COMPANY_NAME = FAKER.company().name();
                    String COMPANY_URL = FAKER.company().url();
                    return getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(companyList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/companies/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(companyIds.get(0))));
    }

    @Test
    @DisplayName("GET /api/companies/: no resources found - Success")
    void findAllWhenNoResourcesFoundReturnsSuccess() throws Exception {

        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/companies/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned content
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/companies/{id}: delete resource found - Success")
    void deleteWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Setup the mock service
        doReturn(mockedEntity).when(service).delete(COMPANY_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    @DisplayName("DELETE /api/companies/{id}: resource not found - Failure")
    void deleteWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Company", COMPANY_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).delete(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("DELETE operation. Component type [Company] and id [%s] was not found", COMPANY_ID))));

        // Verify behavior
        verify(service, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("POST /api/companies: resource create - Success")
    void createWhenResourceCreateReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setName(COMPANY_NAME);
        postEntity.setUrl(COMPANY_URL);
        CompanyDTO mockedEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/companies/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/companies/" + COMPANY_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)));
    }

    @Test
    @DisplayName("POST /api/companies: resource already exists - Failure")
    void createWhenResourceAlreadyExistsReturnsFailure() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO postEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{"Company", COMPANY_NAME});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/companies/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Entity type [Company] with key [%s] already exits in the system", COMPANY_NAME))));
    }

    @Test
    @DisplayName("POST /api/companies: resource name not informed - Failure")
    void createWhenNameAndSiteNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setId(COMPANY_ID);

        // Execute the POST request
        mockMvc.perform(post("/api/companies/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    @Test
    @DisplayName("PUT /api/companies/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setUrl(COMPANY_URL);
        CompanyDTO mockedDTOEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), ArgumentMatchers.any(CompanyDTO.class));

        // Execute the PUT request
        mockMvc.perform(put("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/companies/" + COMPANY_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }

    @Test
    @DisplayName("PUT /api/companies/{id}: resource not found - Failure")
    void updateWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        CompanyDTO postEntity = CompanyDTO.builder().id(COMPANY_ID).build();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Company", COMPANY_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).update(COMPANY_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(put("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Component type [Company] and id [%s] was not found", COMPANY_ID))));
    }
}
