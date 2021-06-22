package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
class ProspectControllerTodoTest extends BaseLightControllerTest<ProspectDTO, Prospect, ProspectService, ProspectMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProspectService service;

    @Autowired
    public ProspectControllerTodoTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory,
                ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI,
                DBConstants.PROSPECT_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("POST /api/prospects/{id}/todos: prospect id and a single valid TODO - Success")
    void addTODOsWhenProspectIdAndOneSingleTODOValidReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        List<TodoDTO> postEntityList = Collections.singletonList(getMockFactory().getTodoMockFactory().newDTO(null));
        List<String> todoIds = new ArrayList<>();
        List<TodoDTO> createdTODOs = getCreatedTODOs(postEntityList, todoIds);

        // setup the mocked service
        doReturn(createdTODOs).when(service).addTODOList(eq(PROSPECT_ID), anyList());

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntityList)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getAddTODOsHeader(PROSPECT_ID, todoIds)))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/prospects/{id}/todos: prospect id and single invalid TODO - Success")
    void addTODOsWhenProspectIdAndOneSingleTODONotValidReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);
        postEntity.setScheduled(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(Collections.singletonList(postEntity))))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("in component [ProspectControllerDelegator] can not be null"))));
    }

    @Test
    @DisplayName("POST /api/prospects/{id}/todos: prospect not found and one single valid TODO - Success")
    void addTODOWhenProspectNotFoundAndTODOValidReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(PROSPECT_ID) });

        // setup the mocked service
        doThrow(exception).when(service).addTODOList(anyString(), anyList());

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(Collections.singletonList(postEntity))))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("POST /api/prospects/{id}/todos: prospect found but TODO valid but already scheduled - Failure")
    void addTODOWhenProspectFoundAndTODOAlreadyScheduledReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        TodoDTO postEntity = getMockFactory().getTodoMockFactory().newDTO(null);

        // setup the mocked service
        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.PROSPECT_TODO_ADD_PROSPECT_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                new String[]{ PROSPECT_ID, postEntity.getScheduled().toString() });
        doThrow(exception).when(service).addTODOList(anyString(), anyList());

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(Collections.singletonList(postEntity))))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/prospects/{id}/todos: prospect id and a list of valid TODOs - Success")
    void addTODOsWhenProspectIdAndTODOsValidReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        List<TodoDTO> postEntityList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj((i) -> getMockFactory().getTodoMockFactory().newDTO(null)).collect(Collectors.toList());
        List<String> todoIds = new ArrayList<>();
        List<TodoDTO> createdTODOs = getCreatedTODOs(postEntityList, todoIds);

        // setup the mocked service
        doReturn(createdTODOs).when(service).addTODOList(eq(PROSPECT_ID), anyList());

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntityList)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getAddTODOsHeader(PROSPECT_ID, todoIds)))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_LIST_SIZE)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/todos/{todoIds}: prospect and TODO found - Success")
    void removeTODOsWhenProspectFoundAndOneSingleTODOFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO todoToBeDeleted =
                getMockFactory().getTodoMockFactory().newDTO(TODO_ID);

        // setup the mocked service
        doReturn(Collections.singletonList(todoToBeDeleted)).when(service).removeTODOList(anyString(), anyList());

        // Execute the DELETE request
        mockMvc.perform(delete(
                getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoIds}",
                PROSPECT_ID,
                Collections.singletonList(TODO_ID)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(TODO_ID)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/todos/{todoId}: prospect not found - Success")
    void removeTODOsWhenProspectNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{getEntityType(), String.valueOf(PROSPECT_ID)});

        // setup the mocked service
        doThrow(exception).when(service).removeTODOList(anyString(), anyList());

        // Execute the POST request
        mockMvc.perform(delete(
                getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoIds}",
                PROSPECT_ID,
                Collections.singletonList(TODO_ID)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/todos/{todoId}: prospect not found and TODO found - Success")
    void removeTODOsWhenTODONotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{DBConstants.TODO_DOCUMENT, String.valueOf(TODO_ID)});

        // setup the mocked service
        doThrow(exception).when(service).removeTODOList(anyString(), anyList());

        // Execute the POST request
        mockMvc.perform(delete(
                getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}",
                PROSPECT_ID,
                Collections.singletonList(TODO_ID)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.TODO_DOCUMENT),
                        TODO_ID)));
    }


    @Test
    @DisplayName("DELETE /api/prospects/{id}/todos/{todoIds}: prospect and TODO found - Success")
    void removeTODOsWhenProspectFoundAndTODOsFoundReturnsSuccess() throws Exception {
        // Set up mocks
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        List<String> todoIds = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj((i) -> getMockFactory().getFAKER().internet().uuid())
                .collect(Collectors.toList());
        List<TodoDTO> todosToBeDeleted = todoIds
                .stream().map(id -> getMockFactory().getTodoMockFactory().newDTO(id))
                .collect(Collectors.toList());

        // setup the mocked service
        doReturn(todosToBeDeleted).when(service).removeTODOList(anyString(), anyList());

        // Execute the DELETE request
        mockMvc.perform(delete(
                getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoIds}",
                PROSPECT_ID,
                todoIds))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_LIST_SIZE)))
                .andExpect(jsonPath("$[0].id", is(todoIds.get(0))));
    }


    @Test
    @DisplayName("GET /api/prospects/{id}/todos: prospect found and TODO list - Success")
    void getTODOsWhenProspectFoundAndTODOListFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        UserCommon user = UserCommon.builder()
                .fullName(USER_FULL_NAME)
                .id(USER_ID)
                .type(UserCommonType.USER).build();
        List<TodoAssigneeDTO> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj((i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    TodoAssigneeDTO todo = getMockFactory().getTodoAssigneeMockFactory().newDTO(TODO_ID);
                    todo.setAssignee(user);
                    return todo;
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(todoList).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID))

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
    @DisplayName("GET /api/prospects/{id}/todos: prospect found and TODO list is empty- Success")
    void getTODOsWhenProspectFoundAndTODOListEmptyReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 0;

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(TODO_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/todos: prospect not found - Failure")
    void getTODOsWhenProspectNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{getEntityType(), PROSPECT_ID});

        // setup the mocked service
        doThrow(exception).when(service).getTODOs(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME, PROSPECT_ID))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/todos/{todoId}: prospect and TODO found - Success")
    void getTODOWhenProspectFoundAndTODOFoundReturnsSuccess() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
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
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", PROSPECT_ID, TODO_ID))

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
    @DisplayName("GET /api/prospects/{id}/todos/{todoId}: prospect not found - Failure")
    void getTODOWhenProspectNotFoundReturnsFailure() throws Exception {
        // Set up mocks
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{getEntityType(), String.valueOf(PROSPECT_ID)});

        // setup the mocked service
        doThrow(exception).when(service).getTODO(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}", PROSPECT_ID, TODO_ID))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("GET /api/prospects/{id}/todos/search: prospect not found - Success")
    void searchTODOWhenTODOsFoundReturnsSuccess() throws Exception {
        String PROSPECT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        doReturn(Collections.emptyList()).when(service).searchBy(any(TodoSearch.class));

        // Execute the GET request
        mockMvc.perform(get(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME +
                        "/search", PROSPECT_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        // Verify
        ArgumentCaptor<TodoSearch> todoSearchCaptor = ArgumentCaptor.forClass(TodoSearch.class);
        verify(service, atMostOnce()).searchBy(todoSearchCaptor.capture());

        Assertions.assertTrue(
                todoSearchCaptor.getValue().getComponentIds().contains(PROSPECT_ID),
                String.format("TodoSearch componentIds must include [%s]", PROSPECT_ID));
    }

    private String getAddTODOsHeader(String prospectId, List<String> todoIds) {
        return
                getResourceURI() +
                        "/" +
                        prospectId +
                        "/" +
                        ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME +
                        "/" +
                        String.join(",", todoIds);
    }

    private List<TodoDTO> getCreatedTODOs(List<TodoDTO> postEntityList, List<String> todoIds) {
        return postEntityList
                .stream()
                .map(t -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    todoIds.add(TODO_ID);
                    return getMockFactory().getTodoMockFactory().newDTO(TODO_ID, t);
                })
                .collect(Collectors.toList());
    }
}
