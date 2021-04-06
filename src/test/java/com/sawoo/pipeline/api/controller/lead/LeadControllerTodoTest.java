package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
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
class LeadControllerTodoTest extends BaseLightControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadControllerTodoTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("POST /api/leads/{id}/todos: lead id and todo valid - Success")
    void addTODOWhenLeadIdAndTODOValidReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);
        TodoDTO createdTODO = getMockFactory().getTodoMockFactory().newDTO(TODO_ID, postEntity);

        // setup the mocked service
        doReturn(createdTODO).when(service).addTODO(anyString(), any(TodoDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + LEAD_ID + "/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/" + TODO_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(TODO_ID)));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/todos: lead id null and TODO valid - Success")
    void addTODOWhenLeadIdAndTODONotValidReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);
        postEntity.setScheduled(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("in component [LeadControllerDelegator] can not be null"))));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/todos: lead not found and TODO valid - Success")
    void addTODOWhenLeadNotFoundAndTODOValidReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).addTODO(anyString(), any(TodoDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("POST /api/leads/{id}/todos: lead not found and TODO valid - Failure")
    void addTODOWhenLeadFoundAndTODOAlreadyScheduledReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);

        // setup the mocked service
        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.LEAD_TODO_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                new String[]{ LEAD_ID, postEntity.getScheduled().toString() });
        doThrow(exception).when(service).addTODO(anyString(), any(TodoDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError());
                /*.andExpect(jsonPath("$.message", stringContainsInOrder(
                        "Lead add TODO exception for lead id",
                        LEAD_ID)));*/
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/todos/{todoId}: lead and TODO found - Success")
    void removeTODOWhenLeadFoundAndTODOFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO todoToBeDeleted =
                getMockFactory().getTodoMockFactory().newDTO(TODO_ID);

        // setup the mocked service
        doReturn(todoToBeDeleted).when(service).removeTODO(anyString(), anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", LEAD_ID, TODO_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(TODO_ID)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/todos/{todoId}: lead not found - Success")
    void removeTODOWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).removeTODO(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", LEAD_ID, TODO_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("DELETE /api/leads/{id}/todos/{todoId}: lead not found and TODO found - Success")
    void removeTODOWhenTODONotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.TODO_DOCUMENT, String.valueOf(TODO_ID) });

        // setup the mocked service
        doThrow(exception).when(service).removeTODO(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", LEAD_ID, TODO_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.TODO_DOCUMENT),
                        TODO_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/todos: lead found and TODO list - Success")
    void getTODOsWhenLeadFoundAndTODOListFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        List<TodoAssigneeDTO> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    TodoAssigneeDTO todo = getMockFactory().getTodoAssigneeMockFactory().newDTO(TODO_ID);
                    todo.setAssignee(user);
                    return todo;
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(todoList).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].scheduled").exists())
                .andExpect(jsonPath("$.[0].id", is(todoList.get(0).getId())))
                .andExpect(jsonPath("$.[0].assignee").exists())
                .andExpect(jsonPath("$.[0].assignee.id", is(USER_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/todos: lead found and TODO list is empty- Success")
    void getTODOsWhenLeadFoundAndTODOListEmptyReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 0;

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/todos: lead not found - Failure")
    void getTODOsWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), LEAD_ID });

        // setup the mocked service
        doThrow(exception).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, LEAD_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/todos/{todoId}: lead and TODO found - Success")
    void getTODOWhenLeadFoundAndTODOFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoAssigneeDTO todoMock = getMockFactory()
                .getTodoAssigneeMockFactory()
                .newDTO(TODO_ID);
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        todoMock.setAssignee(user);


        // setup the mocked service
        doReturn(todoMock).when(service).getTODO(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", LEAD_ID, TODO_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(TODO_ID)))
                .andExpect(jsonPath("$.assignee").exists())
                .andExpect(jsonPath("$.assignee.id", is(USER_ID)))
                .andExpect(jsonPath("$.assignee.fullName", is(USER_FULL_NAME)));
    }

    @Test
    @DisplayName("GET /api/leads/{id}/todos/{todoId}: lead not found - Failure")
    void getTODOWhenLeadNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(LEAD_ID) });

        // setup the mocked service
        doThrow(exception).when(service).getTODO(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", LEAD_ID, TODO_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        LEAD_ID)));
    }
}
