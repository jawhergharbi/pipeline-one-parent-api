package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
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
class ProspectControllerSequenceTodoTest extends BaseLightControllerTest<ProspectDTO, Prospect, ProspectService, ProspectMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProspectService service;

    @Autowired
    public ProspectControllerSequenceTodoTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory,
                ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI,
                DBConstants.PROSPECT_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/sequences/{sequenceId}/todos/eval: evaluate todo to be created based on a sequence and a prospect - Success")
    void evalTODOsWhenProspectAndSequenceFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        int TODO_SIZE = 2;
        List<TodoAssigneeDTO> todos = getTODOs(TODO_SIZE);

        // setup the mocked service
        doReturn(todos).when(service).evalTODOs(PROSPECT_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() +
                "/{id}/" +
                ControllerConstants.SEQUENCE_CONTROLLER_RESOURCE_NAME +
                "/{sequenceId}/" +
                ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME +
                "/eval",
                PROSPECT_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_SIZE)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/sequences/{sequenceId}/todos/eval: prospect not found - Failure")
    void evalTODOsWhenProspectNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = "wrong_prospect_id";
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), PROSPECT_ID });
        doThrow(exception).when(service).evalTODOs(PROSPECT_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.SEQUENCE_CONTROLLER_RESOURCE_NAME +
                        "/{sequenceId}/" +
                        ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME +
                        "/eval",
                PROSPECT_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the response code and content type
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/sequences/{sequenceId}/todos/eval: prospect not found - Failure")
    void evalTODOsWhenSequenceNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = "wrong_sequence_id";

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.SEQUENCE_DOCUMENT, SEQUENCE_ID });
        doThrow(exception).when(service).evalTODOs(PROSPECT_ID, SEQUENCE_ID, null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() +
                "/{id}/" +
                ControllerConstants.SEQUENCE_CONTROLLER_RESOURCE_NAME +
                "/{sequenceId}/" +
                ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME +
                "/eval",
                PROSPECT_ID, SEQUENCE_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the response code and content type
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.SEQUENCE_DOCUMENT),
                        SEQUENCE_ID)));
    }


    private List<TodoAssigneeDTO> getTODOs(int todoSize) {
        return getTODOs(todoSize, getMockFactory().getFAKER().internet().uuid());
    }

    private List<TodoAssigneeDTO> getTODOs(int todoSize, String assigneeId) {
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = assigneeId != null ? assigneeId : getMockFactory().getFAKER().internet().uuid();
        UserCommon assignee = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        return IntStream
                .range(0, todoSize)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    TodoAssigneeDTO todo = getMockFactory().getTodoAssigneeMockFactory().newDTO(TODO_ID);
                    todo.setAssignee(assignee);
                    return todo;
                }).collect(Collectors.toList());
    }
}
