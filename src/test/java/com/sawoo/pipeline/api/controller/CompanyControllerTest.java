package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.service.CompanyService;
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
public class CompanyControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService service;

    @Test
    @DisplayName("GET /api/companies/{id}: company found - Success")
    void getByIdWhenCompanyFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
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
                .andExpect(jsonPath("$.id", is(COMPANY_ID.intValue())))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }

    @Test
    @DisplayName("GET /api/companies/{id}: company not found - Failure")
    void getByIdWhenCompanyNotFoundReturnsResourceNoFoundException() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);

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
    @DisplayName("GET /api/companies/: two companies found - Success")
    void findAllWhenThereAreCompaniesReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<Long> companyIds = new ArrayList<>();
        List<CompanyDTO> companyList = IntStream.range(0, 2)
                .mapToObj((company) -> {
                    Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
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
                .andExpect(jsonPath("$[0].id", is(companyIds.get(0).intValue())));
    }

    @Test
    @DisplayName("GET /api/companies/: no companies found - Success")
    void findAllWhenNoCompanyFoundReturnsSuccess() throws Exception {

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
    @DisplayName("DELETE /api/companies/{id}: delete company found - Success")
    void deleteWhenUserFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Setup the mock service
        doReturn(Optional.of(mockedEntity)).when(service).delete(COMPANY_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/companies/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID.intValue())))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    @DisplayName("DELETE /api/companies/{id}: company not found - Failure")
    void deleteWhenCompanyNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).delete(COMPANY_ID);

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
    }

    @Test
    @DisplayName("POST /api/companies: company create - Success")
    void createWhenCompanyCreateReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
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
                .andExpect(jsonPath("$.id", is(COMPANY_ID.intValue())))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)));
    }

    @Test
    @DisplayName("POST /api/companies: company already exists - Failure")
    void createWhenCompanyAlreadyExistsReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setId(COMPANY_ID);
        postEntity.setName(COMPANY_NAME);
        postEntity.setUrl(COMPANY_URL);

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
    @DisplayName("POST /api/companies: company name not informed - Failure")
    void createWhenNameAndSiteNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
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
    @DisplayName("PUT /api/companies/{id}: company exists - Success")
    void updateWhenCompanyFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setUrl(COMPANY_URL);
        CompanyDTO mockedDTOEntity = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // setup the mocked service
        doReturn(Optional.of(mockedDTOEntity)).when(service).update(COMPANY_ID, postEntity);

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
                .andExpect(jsonPath("$.id", is(COMPANY_ID.intValue())))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }

    @Test
    @DisplayName("PUT /api/companies/{id}: company not found - Failure")
    void updateWhenCompanyNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setId(COMPANY_ID);

        // setup the mocked service
        doReturn(Optional.empty()).when(service).update(COMPANY_ID, postEntity);

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
