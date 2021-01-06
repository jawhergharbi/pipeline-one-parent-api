package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadInteractionControllerTest extends BaseLightControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadInteractionControllerTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("POST /api/leads/{i}/{interactions}: lead id and interaction valid - Success")
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
    @DisplayName("POST /api/leads/{i}/{interactions}: lead id null and interaction valid - Success")
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
    @DisplayName("POST /api/leads/{i}/{interactions}: lead not found and interaction valid - Success")
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
    @DisplayName("POST /api/leads/{i}/{interactions}: lead not found and interaction valid - Success")
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
    @DisplayName("DELETE /api/leads/{i}/{interactions}: lead and interaction found - Success")
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
    @DisplayName("DELETE /api/leads/{i}/{interactions}: lead not found and interaction found - Success")
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
    @DisplayName("DELETE /api/leads/{i}/{interactions}: lead not found and interaction found - Success")
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
}
