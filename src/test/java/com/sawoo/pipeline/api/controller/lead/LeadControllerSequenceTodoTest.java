package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class LeadControllerSequenceTodoTest extends BaseLightControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadControllerSequenceTodoTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("GET /api/leads/{id}/sequences/{sequenceId}/todos/eval: evaluate interaction to be created based on a sequence and a lead - Success")
    void evalInteractionsWhenLeadAndSequenceFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        int INTERACTIONS_SIZE = 2;
        List<TodoAssigneeDTO> interactions = getInteractions(INTERACTIONS_SIZE);

        // setup the mocked service
        doReturn(interactions).when(service).evalInteractions(LEAD_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/sequences/{sequenceId}/interactions/eval", LEAD_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(INTERACTIONS_SIZE)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/sequences/{sequenceId}/todos/eval: lead not found - Failure")
    void evalInteractionsWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = "wrong_lead_id";
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), LEAD_ID });
        doThrow(exception).when(service).evalInteractions(LEAD_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/sequences/{sequenceId}/interactions/eval", LEAD_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the response code and content type
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/sequences/{sequenceId}/todos/eval: lead not found - Failure")
    void evalInteractionsWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = "wrong_sequence_id";

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.SEQUENCE_DOCUMENT, SEQUENCE_ID });
        doThrow(exception).when(service).evalInteractions(LEAD_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/sequences/{sequenceId}/interactions/eval", LEAD_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the response code and content type
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.SEQUENCE_DOCUMENT),
                        SEQUENCE_ID)));
    }

    private List<TodoAssigneeDTO> getInteractions(int interactionSize) {
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        return IntStream
                .range(0, interactionSize)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    TodoAssigneeDTO interaction = getMockFactory().getInteractionAssigneeMockFactory().newDTO(INTERACTION_ID);
                    interaction.setAssignee(user);
                    return interaction;
                }).collect(Collectors.toList());
    }
}
