package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.todo.TodoMapper;
import com.sawoo.pipeline.api.service.todo.TodoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadTodoServiceTest extends BaseLightServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @MockBean
    private TodoService todoService;

    @MockBean
    private LeadServiceDecoratorHelper helper;

    @Autowired
    public LeadTodoServiceTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory, DBConstants.LEAD_DOCUMENT, service);

    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("addTODO: lead does exist and lead todo is valid - Success")
    void addTODOWhenLeadExistsAndLeadTODOValidReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        TodoDTO todoMock = getMockFactory().getTodoMockFactory().newDTO(null);
        TodoDTO todoCreated = getMockFactory().getTodoMockFactory().newDTO(TODO_ID, todoMock);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(todoCreated).when(todoService).create(any(TodoDTO.class));
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().addTODO(LEAD_ID, todoCreated);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new todo", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead todo can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TODO_ID)));

        Assertions.assertFalse(
                spyLeadEntity.getTodos().isEmpty(),
                String.format("Todo list can not be empty for lead id [%s]", LEAD_ID));

        verify(spyLeadEntity, atLeast(1)).getTodos();
        verify(spyLeadEntity, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("addTODO: lead does not exist and TODO on is valid - Failure")
    void addTODOWhenLeadDoesNotExistAndTODOValidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addTODO(LEAD_ID, todoToBeCreated),
                "addTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("addTODO: lead does exist and todo not valid - Failure")
    void addTODOWhenLeadDoesExistAndTODONotValidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        todoToBeCreated.setScheduled(null);

        // Asserts
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.addTODO(LEAD_ID, todoToBeCreated),
                "addTODO must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("addTODO: lead does exist and todo is valid - Failure")
    void addTODOWhenLeadDoesExistAndTODOAlreadyScheduledReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String LEAD_TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(LEAD_TODO_ID);
        todo.setScheduled(todoToBeCreated.getScheduled());
        spyLeadEntity.getTodos().add(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.addTODO(LEAD_ID, todoToBeCreated),
                "addTODO must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.LEAD_TODO_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("addTODO: lead does exist and each lead todo is valid - Success")
    void addTODOListWhenLeadExistsAndLeadTODOValidReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Execute the service call
        /*List<TodoDTO> returnedDTOList = getService().addTODOList(LEAD_ID, null);*/
    }

    @Test
    @DisplayName("removeTODO: lead does exist and lead todo found - Success")
    void removeTODOWhenLeadExistsAndLeadTODOFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
        spyLeadEntity.getTodos().add(todo);
        TodoDTO todoDTO = (new TodoMapper()).getMapperOut().getDestination(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(todoDTO).when(todoService).delete(anyString());

        // Execute the service call
        TodoDTO returnedDTO = getService().removeTODO(LEAD_ID, TODO_ID);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new todo", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead todo can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TODO_ID)));

        Assertions.assertTrue(
                spyLeadEntity.getTodos().isEmpty(),
                String.format("Todo list must be empty for lead id [%s]", LEAD_ID));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("removeTODO: lead does not exist - Failure")
    void removeTODOWhenLeadDoesNotExistReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeTODO(LEAD_ID, TODO_ID),
                "removeTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("removeTODO: todo id is null - Failure")
    void removeTODOWhenTODOIdNullReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Asserts
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.removeTODO(LEAD_ID, null),
                "removeTODO must throw a ConstraintViolationException");

        String exceptionMessage = exception.getMessage();
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exceptionMessage));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("getTODOs: lead found - Success")
    void getTODOsWhenLeadFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();
        doReturn(Collections.emptyList()).when(helper).getUsers(anyString());

        // Execute the service call
        List<TodoAssigneeDTO> returnedListDTO = getService().getTODOs(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of todos", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(
                        TODO_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Todo list size must be [%d]", TODO_LIST_SIZE)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getTODOs: lead found - Success")
    void getTODOsWhenLeadFoundAndAccountFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 10;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<UserCommon> mockedUsers = IntStream
                .range(0, 2)
                .mapToObj( (u) -> {
                    String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
                    String USER_ID = getMockFactory().getFAKER().internet().uuid();
                    return UserCommon.builder().fullName(USER_FULL_NAME).id(USER_ID).build();
                }).collect(Collectors.toList());

        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                    todo.setAssigneeId(mockedUsers
                            .get(getMockFactory()
                                    .getFAKER()
                                    .random()
                                    .nextInt(1)).getId());
                    return todo;
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();
        doReturn(mockedUsers).when(helper).getUsers(anyString());

        // Execute the service call
        List<TodoAssigneeDTO> returnedListDTO = getService().getTODOs(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of todos", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(
                        TODO_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Todo list size must be [%d]", TODO_LIST_SIZE)),
                () -> Assertions.assertNotNull(returnedListDTO.get(0).getAssignee(), "Assignee can not be null"));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getTODOs: lead found - Failure")
    void getTODOsWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODOs(LEAD_ID),
                "getTodos must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getTODO: lead and todo found - Success")
    void getTODOWhenLeadAndTODOFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);
        int TODO_IDX = new Random().nextInt(3);
        String TARGET_TODO_ID = todoList.get(TODO_IDX).getId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().getTODO(LEAD_ID, TARGET_TODO_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] contains the searched todo", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Todo can not be null"),
                () -> Assertions.assertEquals(
                        TARGET_TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TARGET_TODO_ID)));
    }

    @Test
    @DisplayName("getTODO: lead not found - Success")
    void getTODOWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODO(LEAD_ID, TODO_ID),
                "getTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getTODO: lead found and todo not found - Success")
    void getTODOWhenLeadFoundAndTODONotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);
        String TARGET_TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODO(LEAD_ID, TARGET_TODO_ID),
                "getTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }
}
