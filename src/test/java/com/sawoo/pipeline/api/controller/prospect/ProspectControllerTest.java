package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.controller.base.BaseControllerTestOld;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
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
public class ProspectControllerTest extends BaseControllerTestOld {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProspectService service;

    @Test
    @DisplayName("GET /api/prospects/{id}: resource found - Success")
    void getByIdWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO mockedDTOEntity = getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        // Setup the mock service
        doReturn(mockedDTOEntity).when(service).findById(PROSPECT_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.firstName", is(PROSPECT_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(PROSPECT_LAST_NAME)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}: resource not found - Failure")
    void getByIdWhenResourceNotFoundReturnsResourceNoFoundException() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Prospect", String.valueOf(PROSPECT_ID)});

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(PROSPECT_ID);

        // Execute the GET request
        mockMvc.perform(get("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("GET operation. Component type [Prospect] and id [%s] was not found", PROSPECT_ID))));
    }

    @Test
    @DisplayName("GET /api/prospects/: any resource found - Success")
    void findAllWhenResourcesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<String> entityIds = new ArrayList<>();
        List<ProspectDTO> entityList = IntStream.range(0, 2)
                .mapToObj((entity) -> {
                    String PROSPECT_ID = FAKER.internet().uuid();
                    entityIds.add(PROSPECT_ID);
                    String PROSPECT_FIRST_NAME = FAKER.name().firstName();
                    String PROSPECT_LAST_NAME = FAKER.name().lastName();
                    return getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(entityList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(entityIds.get(0))));
    }

    @Test
    @DisplayName("GET /api/prospect/: no resources found - Success")
    void findAllWhenNoResourcesFoundReturnsSuccess() throws Exception {

        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned content
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}: delete resource found - Success")
    void deleteWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO mockedEntity = getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        // Setup the mock service
        doReturn(mockedEntity).when(service).delete(PROSPECT_ID);

        // Execute the GET request
        mockMvc.perform(delete("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.firstName", is(PROSPECT_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(PROSPECT_LAST_NAME)))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}: resource not found - Failure")
    void deleteWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Prospect", PROSPECT_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).delete(PROSPECT_ID);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("DELETE operation. Component type [Prospect] and id [%s] was not found",
                                        PROSPECT_ID))));

        // Verify behavior
        verify(service, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("POST /api/prospects: resource create - Success")
    void createWhenResourceCreateReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO postEntity = getMockFactory().newProspectBaseDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);
        ProspectDTO mockedEntity = getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/prospects/" + PROSPECT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.firstName", is(PROSPECT_FIRST_NAME)));
    }

    @Test
    @DisplayName("POST /api/prospects: resource already exists - Failure")
    void createWhenResourceAlreadyExistsReturnsFailure() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO postEntity = getMockFactory().newProspectBaseDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{"Prospect", postEntity.getLinkedInUrl()});

        // setup the mocked service
        doThrow(exception)
                .when(service).create(postEntity);

        // Execute the POST request
        mockMvc.perform(post("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Entity type [Prospect] with key [%s] already exits in the system", postEntity.getLinkedInUrl()))));
    }

    @Test
    @DisplayName("POST /api/prospect: resource firstName not informed - Failure")
    void createWhenFirstNameNotInformedReturnsFailure() throws Exception {
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO postEntity = getMockFactory().newProspectBaseDTO(PROSPECT_ID, null, PROSPECT_LAST_NAME);

        // Execute the POST request
        mockMvc.perform(post("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("PUT /api/prospects/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        String PROSPECT_POSITION = FAKER.company().profession();
        ProspectDTO postEntity = new ProspectDTO();
        postEntity.setPosition(PROSPECT_POSITION);
        ProspectDTO mockedDTOEntity = getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), ArgumentMatchers.any(ProspectDTO.class));

        // Execute the PUT request
        mockMvc.perform(put("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/prospects/" + PROSPECT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.firstName", is(PROSPECT_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(PROSPECT_LAST_NAME)));
    }

    @Test
    @DisplayName("PUT /api/prospects/{id}: resource not found - Failure")
    void updateWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        ProspectDTO postEntity = new ProspectDTO();
        postEntity.setLinkedInUrl(PROSPECT_ID);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"Prospect", PROSPECT_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).update(PROSPECT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(put("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(String.format("Component type [Prospect] and id [%s] was not found", PROSPECT_ID))));
    }
}
