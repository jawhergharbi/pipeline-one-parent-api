package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class SequenceControllerTest extends BaseControllerTest<SequenceDTO, Sequence, SequenceService, SequenceMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SequenceService service;

    @Autowired
    public SequenceControllerTest(SequenceMockFactory mockFactory, SequenceService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI,
                DBConstants.SEQUENCE_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "id";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("name", "description", "created");
    }

    @Override
    protected Class<SequenceDTO> getDTOClass() {
        return SequenceDTO.class;
    }

    @Test
    @DisplayName("POST /api/sequences: resource name and users not informed - Failure")
    void createWhenNameNotInformedAndUsersNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        SequenceDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setName(null);
        postEntity.setUsers(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/sequences: resource componentId - Failure")
    void createWhenComponentIdNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        SequenceDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setComponentId(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/sequences: user id not informed - Failure")
    void createWhenUserIdNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        SequenceDTO postEntity = getMockFactory().newDTO(null);
        postEntity.getUsers().forEach(u -> u.setUserId(null));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}: user id not informed - Failure")
    void createWhenUserOwnerNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        SequenceDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setUsers(new HashSet<>(Collections.singleton(
                        SequenceUserDTO
                                .builder()
                                .userId(USER_ID)
                                .type(SequenceUserType.EDITOR)
                                .build())));

        // setup the mocked service
        CommonServiceException exception = new  CommonServiceException(
                ExceptionMessageConstants.SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION,
                new String[] {USER_ID});
        doThrow(exception).when(service).create(any(SequenceDTO.class));

        // Execute the PUT request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", containsString("User owner has not been informed.")));
    }

    @Test
    @DisplayName("PUT /api/sequences/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdateNameReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        String NEW_NAME = getMockFactory().getFAKER().funnyName().name();
        SequenceDTO postEntity = new SequenceDTO();
        postEntity.setName(NEW_NAME);
        SequenceDTO mockedDTO = getMockFactory().newDTO(SEQUENCE_ID);
        mockedDTO.setName(NEW_NAME);

        // setup the mocked service
        doReturn(mockedDTO).when(service).update(anyString(), any(SequenceDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + SEQUENCE_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(SEQUENCE_ID)))
                .andExpect(jsonPath("$.name", is(NEW_NAME)));
    }

    @Test
    @DisplayName("PUT /api/sequences/{id}: user id not informed - Failure")
    void updateWhenUserIdNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceDTO postEntity = SequenceDTO.builder()
                .users(new HashSet<>(Collections.singleton(
                        SequenceUserDTO
                                .builder()
                                .type(SequenceUserType.OWNER)
                                .build())))
                .id(SEQUENCE_ID)
                .build();

        // setup the mocked service
        CommonServiceException exception = new  CommonServiceException(
                ExceptionMessageConstants.SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION,
                new String[] {SEQUENCE_ID});
        doThrow(exception).when(service).update(anyString(), any(SequenceDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", containsString("User id has not been informed.")));
    }

    @Test
    @DisplayName("DELETE /api/sequences/{id}/user/{userId}: sequence found and user is not the owner - Success")
    void deleteUserWhenUserFoundAndIsNotOwnerReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        SequenceDTO mockedEntity = getMockFactory().newDTO(SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteUser(SEQUENCE_ID, USER_ID);

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/user/{userId}", SEQUENCE_ID, USER_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + SEQUENCE_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(SEQUENCE_ID)))
                .andExpect(jsonPath("$.users", hasSize(1)));
    }
}
