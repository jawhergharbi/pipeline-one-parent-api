package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.lead.Lead;
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
    @DisplayName("addInteraction: lead does exist and lead interaction is valid - Success")
    void addInteractionWhenLeadExistsAndLeadInteractionValidReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        TodoDTO interactionMock = getMockFactory().getTodoMockFactory().newDTO(null);
        TodoDTO interactionCreated = getMockFactory().getTodoMockFactory().newDTO(INTERACTION_ID, interactionMock);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(interactionCreated).when(todoService).create(any(TodoDTO.class));
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().addInteraction(LEAD_ID, interactionCreated);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new interaction", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead interaction can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", INTERACTION_ID)));

        Assertions.assertFalse(
                spyLeadEntity.getTodos().isEmpty(),
                String.format("Todo list can not be empty for lead id [%s]", LEAD_ID));

        verify(spyLeadEntity, atLeast(1)).getTodos();
        verify(spyLeadEntity, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("addInteraction: lead does not exist and interaction on is valid - Failure")
    void addInteractionWhenLeadDoesNotExistAndInteractionValidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO interactionToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("addInteraction: lead does exist and interaction not valid - Failure")
    void addInteractionWhenLeadDoesExistAndInteractionNotValidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        TodoDTO interactionToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        interactionToBeCreated.setScheduled(null);

        // Asserts
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("addInteraction: lead does exist and interaction is valid - Failure")
    void addInteractionWhenLeadDoesExistAndInteractionAlreadyScheduledReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String LEAD_INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO interactionToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(LEAD_INTERACTION_ID);
        todo.setScheduled(interactionToBeCreated.getScheduled());
        spyLeadEntity.getTodos().add(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("removeInteraction: lead does exist and lead interaction found - Success")
    void removeInteractionWhenLeadExistsAndLeadInteractionFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(INTERACTION_ID);
        spyLeadEntity.getTodos().add(todo);
        TodoDTO todoDTO = (new TodoMapper()).getMapperOut().getDestination(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(todoDTO).when(todoService).delete(anyString());

        // Execute the service call
        TodoDTO returnedDTO = getService().removeInteraction(LEAD_ID, INTERACTION_ID);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new todo", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead todo can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", INTERACTION_ID)));

        Assertions.assertTrue(
                spyLeadEntity.getTodos().isEmpty(),
                String.format("Todo list must be empty for lead id [%s]", LEAD_ID));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("removeInteraction: lead does not exist - Failure")
    void removeInteractionWhenLeadDoesNotExistReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeInteraction(LEAD_ID, INTERACTION_ID),
                "removeInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("removeInteraction: interaction id is null - Failure")
    void removeInteractionWhenInteractionIdNullReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Asserts
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.removeInteraction(LEAD_ID, null),
                "removeInteraction must throw a ConstraintViolationException");

        String exceptionMessage = exception.getMessage();
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exceptionMessage));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("getTodos: lead found - Success")
    void getInteractionsWhenLeadFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();
        doReturn(Collections.emptyList()).when(helper).getUsers(anyString());

        // Execute the service call
        List<TodoAssigneeDTO> returnedListDTO = getService().getInteractions(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of todos", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(
                        INTERACTION_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Todo list size must be [%d]", INTERACTION_LIST_SIZE)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getTodos: lead found - Success")
    void getInteractionsWhenLeadFoundAndAccountFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 10;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<UserCommon> mockedUsers = IntStream
                .range(0, 2)
                .mapToObj( (u) -> {
                    String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
                    String USER_ID = getMockFactory().getFAKER().internet().uuid();
                    return UserCommon.builder().fullName(USER_FULL_NAME).id(USER_ID).build();
                }).collect(Collectors.toList());

        List<Todo> todoList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    Todo todo = getMockFactory().getTodoMockFactory().newEntity(INTERACTION_ID);
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
        List<TodoAssigneeDTO> returnedListDTO = getService().getInteractions(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of todos", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(
                        INTERACTION_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Todo list size must be [%d]", INTERACTION_LIST_SIZE)),
                () -> Assertions.assertNotNull(returnedListDTO.get(0).getAssignee(), "Assignee can not be null"));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getTodos: lead found - Failure")
    void getInteractionsWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getInteractions(LEAD_ID),
                "getTodos must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getTodos: lead and interaction found - Success")
    void getInteractionWhenLeadAndInteractionFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);
        int INTERACTION_IDX = new Random().nextInt(3);
        String TARGET_INTERACTION_ID = todoList.get(INTERACTION_IDX).getId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().getInteraction(LEAD_ID, TARGET_INTERACTION_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] contains the searched interaction", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Todo can not be null"),
                () -> Assertions.assertEquals(
                        TARGET_INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TARGET_INTERACTION_ID)));
    }

    @Test
    @DisplayName("getInteraction: lead not found - Success")
    void getInteractionWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getInteraction(LEAD_ID, INTERACTION_ID),
                "getInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getInteraction: lead found and interaction not found - Success")
    void getInteractionWhenLeadFoundAndInteractionNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Todo> todoList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setTodos(todoList);
        String TARGET_INTERACTION_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Asserts
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getInteraction(LEAD_ID, TARGET_INTERACTION_ID),
                "getInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }
}
