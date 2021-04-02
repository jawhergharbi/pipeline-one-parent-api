package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class SequenceControllerTest extends BaseControllerTest<SequenceDTO, Sequence, SequenceService, SequenceMockFactory> {

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

    @Test
    @DisplayName("GET /api/sequences/accounts/{accountIds}/main: sequence found - Success")
    void findByAccountsWhenSequenceEntitiesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getAccountMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceDTO mockedSequence = getMockFactory().newDTO(SEQUENCE_ID);

        // setup the mocked service
        doReturn(Collections.singletonList(mockedSequence)).when(service).findByAccountIds(anySet());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/accounts/{accountIds}/main", new HashSet<>(Collections.singletonList(COMPONENT_ID))))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}/step: step valid and sequence found - Success")
    void addStepWhenSequenceFoundAndStepValidReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceStepDTO postEntity = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
        SequenceStepDTO mockedEntity = getMockFactory().getSequenceStepMockFactory().newDTO(SEQUENCE_STEP_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).addStep(anyString(), any(SequenceStepDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/steps", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + SEQUENCE_ID + "/steps/" + SEQUENCE_STEP_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(SEQUENCE_STEP_ID)))
                .andExpect(jsonPath("$.messageTemplate").exists())
                .andExpect(jsonPath("$.messageTemplate.text", is(mockedEntity.getMessageTemplate().getText())));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}/steps: step invalid position not informed and sequence found - Failure")
    void addStepWhenSequenceStepInvalidPositionNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceStepDTO postEntity = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        postEntity.setPosition(-1);

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/steps", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}/steps: step invalid and sequence found - Failure")
    void addStepWhenSequenceStepInvalidPositionAndPersonalityAndChannelNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceStepDTO postEntity = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        postEntity.setPosition(-1);
        postEntity.setPersonality(null);
        postEntity.setChannel(null);

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/steps", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(3)));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}/steps: step invalid and sequence found - Failure")
    void addStepWhenSequenceStepInvalidPersonalityOutOfRangeReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceStepDTO postEntity = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        postEntity.setPersonality(5);

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/steps", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/sequences/{id}/steps/{stepsId}: sequence not found - Success")
    void addStepWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        SequenceStepDTO postEntity = getMockFactory().getSequenceStepMockFactory().newDTO(null);

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).addStep(anyString(), any(SequenceStepDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/steps", SEQUENCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        SEQUENCE_ID))));
    }

    @Test
    @DisplayName("DELETE /api/sequences/{id}/steps/{stepsId}: sequence and step found - Success")
    void removeStepWhenSequenceAndSequenceStepFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
        SequenceStepDTO mockedStep = getMockFactory().getSequenceStepMockFactory().newDTO(SEQUENCE_STEP_ID);

        // setup the mocked service
        doReturn(mockedStep).when(service).removeStep(anyString(), anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/steps/{stepId}", SEQUENCE_ID, SEQUENCE_STEP_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(SEQUENCE_STEP_ID)))
                .andExpect(jsonPath("$.messageTemplate").exists())
                .andExpect(jsonPath("$.messageTemplate.text", is(mockedStep.getMessageTemplate().getText())));
    }

    @Test
    @DisplayName("DELETE /api/sequences/{id}/steps/{stepsId}: sequence not found - Success")
    void removeStepWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).removeStep(anyString(), anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/steps/{stepId}", SEQUENCE_ID, SEQUENCE_STEP_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        SEQUENCE_ID))));
    }

    @Test
    @DisplayName("PUT /api/sequences/{id}/step/{stepID}: sequence and step found - Success")
    void updateStepWhenSequenceFoundAndStepValidReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEP_POSITION = 2;
        SequenceStepDTO postEntity = SequenceStepDTO.builder().position(STEP_POSITION).build();
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
        SequenceStepDTO mockedEntity = getMockFactory().getSequenceStepMockFactory().newDTO(SEQUENCE_STEP_ID);
        mockedEntity.setPosition(postEntity.getPosition());

        // setup the mocked service
        doReturn(mockedEntity).when(service).updateStep(anyString(), any(SequenceStepDTO.class));

        // Execute the GET request
        mockMvc.perform(put(getResourceURI() + "/{id}/steps/{stepId}", SEQUENCE_ID, SEQUENCE_STEP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + SEQUENCE_ID + "/steps/" + SEQUENCE_STEP_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(SEQUENCE_STEP_ID)))
                .andExpect(jsonPath("$.messageTemplate").exists())
                .andExpect(jsonPath("$.messageTemplate.text", is(mockedEntity.getMessageTemplate().getText())))
                .andExpect(jsonPath("$.position", is(STEP_POSITION)));
    }

    @Test
    @DisplayName("PUT /api/sequences/{id}/steps/{stepsId}: sequence not found - Success")
    void updateStepWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
        SequenceStepDTO postEntity = SequenceStepDTO.builder().position(2).build();

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).updateStep(anyString(), any(SequenceStepDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}/steps/{stepId}", SEQUENCE_ID, SEQUENCE_STEP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        SEQUENCE_ID))));
    }

    @Test
    @DisplayName("GET /api/sequences/{id}/steps: sequence found - Success")
    void getStepsWhenSequenceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ID_LIST_SIZE = 3;
        String SEQUENCE_ID = getMockFactory().getComponentId();
        List<SequenceStepDTO> stepList = IntStream.range(0, ID_LIST_SIZE)
                .mapToObj((step) -> {
                    String COMPONENT_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
                    return getMockFactory().getSequenceStepMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(stepList).when(service).getSteps(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/steps", SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(ID_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/sequences/{id}/steps: sequence found - Failure")
    void getStepsWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).getSteps(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/steps", SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        SEQUENCE_ID))));
    }

    @Test
    @DisplayName("GET /api/sequences/{id}/steps/search-personality: sequence found - Success")
    void getStepsByPersonalityWhenSequenceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ID_LIST_SIZE = 3;
        int PERSONALITY = 1;
        String SEQUENCE_ID = getMockFactory().getComponentId();
        List<SequenceStepDTO> stepList = IntStream.range(0, ID_LIST_SIZE)
                .mapToObj((step) -> {
                    String COMPONENT_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
                    return getMockFactory().getSequenceStepMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(stepList).when(service).getStepsByPersonality(anyString(), any(Integer.class));

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/steps/search-personality", SEQUENCE_ID)
                .param("personality", String.valueOf(PERSONALITY)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(ID_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/sequences/{id}/steps/search-personality: sequence not found - Success")
    void getStepsByPersonalityWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int PERSONALITY = 2;

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).getStepsByPersonality(anyString(), any(Integer.class));

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/steps/search-personality", SEQUENCE_ID)
                .param("personality", String.valueOf(PERSONALITY)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        SEQUENCE_ID))));
    }

    @Test
    @DisplayName("GET /api/sequences/{id}/steps/search-personality: personality wrong - Failure")
    void getStepsByPersonalityWhenPersonalityInvalidReturnsFailure() throws Exception {
        // Setup the mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int PERSONALITY = 5;

        // setup the mocked service
        ResourceNotFoundException exception = new  ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[] {getEntityType(), SEQUENCE_ID});
        doThrow(exception).when(service).getStepsByPersonality(anyString(), any(Integer.class));

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/steps/search-personality", SEQUENCE_ID)
                .param("personality", String.valueOf(PERSONALITY)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath(
                        "$.messages[0]",
                        stringContainsInOrder("Field or param", "in component", "has exceeded its max size")));
    }
}
