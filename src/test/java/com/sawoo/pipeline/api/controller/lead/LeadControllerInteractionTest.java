package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.service.lead.LeadService;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class LeadControllerInteractionTest extends BaseLightControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadControllerInteractionTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("POST /api/leads/{id}/interactions: lead id and interaction valid - Success")
    void addInteractionWhenLeadIdAndInteractionValidReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        InteractionDTO postInteraction = getMockFactory().getInteractionMockFactory().newDTO(null);
        InteractionDTO createdInteraction = getMockFactory().getInteractionMockFactory().newDTO(INTERACTION_ID, postInteraction);

        // setup the mocked service
        doReturn(createdInteraction).when(service).addInteraction(anyString(), any(InteractionDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postInteraction)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + LEAD_ID + "/interactions/" + INTERACTION_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(INTERACTION_ID)));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/interactions: lead id null and interaction valid - Success")
    void addInteractionWhenLeadIdAndInteractionNotValidReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        InteractionDTO postInteraction = getMockFactory().getInteractionMockFactory().newDTO(null);
        postInteraction.setScheduled(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postInteraction)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("in component [LeadControllerDelegator] can not be null"))));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/interactions: lead not found and interaction valid - Success")
    void addInteractionWhenLeadNotFoundAndInteractionValidReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        InteractionDTO postInteraction = getMockFactory().getInteractionMockFactory().newDTO(null);
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).addInteraction(anyString(), any(InteractionDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postInteraction)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/interactions: lead not found and interaction valid - Success")
    void addInteractionWhenLeadFoundAndInteractionAlreadyScheduledReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        InteractionDTO postInteraction = getMockFactory().getInteractionMockFactory().newDTO(null);
        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                new String[]{ LEAD_ID, postInteraction.getScheduled().toString() });

        // setup the mocked service
        doThrow(exception).when(service).addInteraction(anyString(), any(InteractionDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postInteraction)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        "Lead add interaction exception for lead id",
                        LEAD_ID)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/interactions/{interactionId}: lead and interaction found - Success")
    void removeInteractionWhenLeadFoundAndInteractionFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        InteractionDTO interactionToBeDeleted =
                getMockFactory().getInteractionMockFactory().newDTO(INTERACTION_ID);

        // setup the mocked service
        doReturn(interactionToBeDeleted).when(service).removeInteraction(anyString(), anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/interactions/{interactionId}", LEAD_ID, INTERACTION_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(INTERACTION_ID)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/interactions/{interactionId}: lead not found and interaction found - Success")
    void removeInteractionWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).removeInteraction(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/interactions/{interactionId}", LEAD_ID, INTERACTION_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/interactions/{interactionId}: lead not found and interaction found - Success")
    void removeInteractionWhenInteractionNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.INTERACTION_DOCUMENT, String.valueOf(INTERACTION_ID) });

        // setup the mocked service
        doThrow(exception).when(service).removeInteraction(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/interactions/{interactionId}", LEAD_ID, INTERACTION_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.INTERACTION_DOCUMENT),
                        INTERACTION_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/interactions: lead found and interaction list - Success")
    void getInteractionsWhenLeadFoundAndInteractionListFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        List<InteractionAssigneeDTO> interactionList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    InteractionAssigneeDTO interaction = getMockFactory().getInteractionAssigneeMockFactory().newDTO(INTERACTION_ID);
                    interaction.setAssignee(user);
                    return interaction;
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(interactionList).when(service).getInteractions(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/interactions", LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(INTERACTION_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].scheduled").exists())
                .andExpect(jsonPath("$.[0].id", is(interactionList.get(0).getId())))
                .andExpect(jsonPath("$.[0].assignee").exists())
                .andExpect(jsonPath("$.[0].assignee.id", is(USER_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/interactions: lead found and interaction list is empty- Success")
    void getInteractionsWhenLeadFoundAndInteractionListEmptyReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 0;

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).getInteractions(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/interactions", LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(INTERACTION_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/interactions: lead not found - Failure")
    void getInteractionsWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), LEAD_ID });

        // setup the mocked service
        doThrow(exception).when(service).getInteractions(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/interactions", LEAD_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/interactions/{interactionId}: lead and interaction found - Success")
    void getInteractionWhenLeadFoundAndInteractionFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        InteractionAssigneeDTO interactionMock = getMockFactory()
                .getInteractionAssigneeMockFactory()
                .newDTO(INTERACTION_ID);
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        interactionMock.setAssignee(user);


        // setup the mocked service
        doReturn(interactionMock).when(service).getInteraction(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/interactions/{interactionId}", LEAD_ID, INTERACTION_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(INTERACTION_ID)))
                .andExpect(jsonPath("$.assignee").exists())
                .andExpect(jsonPath("$.assignee.id", is(USER_ID)))
                .andExpect(jsonPath("$.assignee.fullName", is(USER_FULL_NAME)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/interactions/{interactionId}: lead not found - Failure")
    void getInteractionWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).getInteraction(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/interactions/{interactionId}", LEAD_ID, INTERACTION_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }
}
