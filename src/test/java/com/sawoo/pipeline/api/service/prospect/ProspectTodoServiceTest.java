package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
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
class ProspectTodoServiceTest extends BaseLightServiceTest<ProspectDTO, Prospect, ProspectRepository, ProspectService, ProspectMockFactory> {

    @MockBean
    private ProspectRepository repository;

    @MockBean
    private TodoService todoService;

    @MockBean
    private ProspectServiceDecoratorHelper helper;

    @Autowired
    public ProspectTodoServiceTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory, DBConstants.PROSPECT_DOCUMENT, service);

    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("addTODO: prospect does exist and prospect todo is valid - Success")
    void addTODOWhenProspectExistsAndProspectTODOValidReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Prospect spyProspectEntity = spy(getMockFactory().newEntity(PROSPECT_ID));
        TodoDTO todoMock = getMockFactory().getTodoMockFactory().newDTO(null);
        TodoDTO todoCreated = getMockFactory().getTodoMockFactory().newDTO(TODO_ID, todoMock);

        // Set up the mocked repository
        doReturn(Optional.of(spyProspectEntity)).when(repository).findById(anyString());
        doReturn(todoCreated).when(todoService).create(any(TodoDTO.class));
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().addTODO(PROSPECT_ID, todoCreated);

        Assertions.assertAll(String.format("Prospect id [%s] must be updated with a new todo", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Prospect todo can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TODO_ID)));

        Assertions.assertFalse(
                spyProspectEntity.getTodos().isEmpty(),
                String.format("Todo list can not be empty for prospect id [%s]", PROSPECT_ID));

        verify(spyProspectEntity, atLeast(1)).getTodos();
        verify(spyProspectEntity, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Prospect.class));
    }

    @Test
    @DisplayName("addTODO: prospect does not exist and TODO on is valid - Failure")
    void addTODOWhenProspectDoesNotExistAndTODOValidReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addTODO(PROSPECT_ID, todoToBeCreated),
                "addTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("addTODO: prospect does exist and todo not valid - Failure")
    void addTODOWhenProspectDoesExistAndTODONotValidReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        todoToBeCreated.setScheduled(null);

        // Asserts
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.addTODO(PROSPECT_ID, todoToBeCreated),
                "addTODO must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("addTODO: prospect does exist and todo is valid - Failure")
    void addTODOWhenProspectDoesExistAndTODOAlreadyScheduledReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String PROSPECT_TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        TodoDTO todoToBeCreated = getMockFactory().getTodoMockFactory().newDTO(null);
        Prospect spyProspectEntity = spy(getMockFactory().newEntity(PROSPECT_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(PROSPECT_TODO_ID);
        todo.setScheduled(todoToBeCreated.getScheduled());
        spyProspectEntity.getTodos().add(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyProspectEntity)).when(repository).findById(anyString());

        // Asserts
        ProspectService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.addTODO(PROSPECT_ID, todoToBeCreated),
                "addTODO must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.PROSPECT_TODO_ADD_PROSPECT_SLOT_ALREADY_SCHEDULED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("addTODOList: prospect does exist and each prospect todo is valid - Success")
    void addTODOListWhenProspectExistsAndProspectTODOValidReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Execute the service call
        /*List<TodoDTO> returnedDTOList = getService().addTODOList(PROSPECT_ID, null);*/
    }

    @Test
    @DisplayName("removeTODO: prospect does exist and prospect todo found - Success")
    void removeTODOWhenProspectExistsAndProspectTODOFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();
        Prospect spyProspectEntity = spy(getMockFactory().newEntity(PROSPECT_ID));
        Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
        spyProspectEntity.getTodos().add(todo);
        TodoDTO todoDTO = (new TodoMapper()).getMapperOut().getDestination(todo);

        // Set up the mocked repository
        doReturn(Optional.of(spyProspectEntity)).when(repository).findById(anyString());
        doReturn(todoDTO).when(todoService).delete(anyString());

        // Execute the service call
        TodoDTO returnedDTO = getService().removeTODO(PROSPECT_ID, TODO_ID);

        Assertions.assertAll(String.format("Prospect id [%s] must be updated with a new todo", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Prospect todo can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TODO_ID)));

        Assertions.assertTrue(
                spyProspectEntity.getTodos().isEmpty(),
                String.format("Todo list must be empty for prospect id [%s]", PROSPECT_ID));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Prospect.class));
    }

    @Test
    @DisplayName("removeTODO: prospect does not exist - Failure")
    void removeTODOWhenProspectDoesNotExistReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeTODO(PROSPECT_ID, TODO_ID),
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
        String PROSPECT_ID = getMockFactory().getComponentId();

        // Asserts
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.removeTODO(PROSPECT_ID, null),
                "removeTODO must throw a ConstraintViolationException");

        String exceptionMessage = exception.getMessage();
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exceptionMessage));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("removeTODOs: prospect does exist and TODOs found - Success")
    void removeTODOsWhenProspectExistsAndProspectTODOsFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        Prospect spyProspectEntity = spy(getMockFactory().newEntity(PROSPECT_ID));
        int TODO_LIST_SIZE = 3;
        List<String> todoIds = new ArrayList<>();
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    todoIds.add(TODO_ID);
                    Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                    todo.setAssigneeId(PROSPECT_ID);
                    return todo;
                }).collect(Collectors.toList());
        spyProspectEntity.setTodos(todoList);
        TodoDTO todoDTO = (new TodoMapper()).getMapperOut().getDestination(todoList.get(0));

        // Set up the mocked repository
        doReturn(Optional.of(spyProspectEntity)).when(repository).findById(anyString());
        doReturn(Collections.singletonList(todoDTO)).when(todoService).deleteByIds(anyList());

        // Execute the service call
        List<TodoDTO> returnedDTOList = getService()
                .removeTODOList(PROSPECT_ID, Collections.singletonList(todoIds.get(0)));

        Assertions.assertAll(String.format("Prospect id [%s] must be updated with a new todo", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedDTOList, "Prospect TODO list can not be null"),
                () -> Assertions.assertEquals(
                        1,
                        returnedDTOList.size(),
                        String.format("Deleted TODO list size must be [%d]", 1)));

        Assertions.assertFalse(
                spyProspectEntity.getTodos().isEmpty(),
                String.format("Todo list can not be empty for prospect id [%s]", PROSPECT_ID));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Prospect.class));
    }

    @Test
    @DisplayName("getTODOs: prospect found - Success")
    void getTODOsWhenProspectFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                    todo.setAssigneeId(PROSPECT_ID);
                    return todo;
                }).collect(Collectors.toList());
        prospectEntity.setTodos(todoList);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();
        doReturn(Collections.emptyList()).when(helper).getUsers(anyString());

        // Execute the service call
        List<TodoAssigneeDTO> returnedListDTO = getService().getTODOs(PROSPECT_ID);

        // Assertions
        Assertions.assertAll(String.format("Prospect with id [%s] has a list of todos", PROSPECT_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(
                        TODO_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Todo list size must be [%d]", TODO_LIST_SIZE)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getTODOs: prospect found - Success")
    void getTODOsWhenProspectFoundAndAccountFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 10;
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
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
        prospectEntity.setTodos(todoList);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();
        doReturn(mockedUsers).when(helper).getUsers(anyString());

        // Execute the service call
        List<TodoAssigneeDTO> returnedListDTO = getService().getTODOs(PROSPECT_ID);

        // Assertions
        Assertions.assertAll(String.format("Prospect with id [%s] has a list of todos", PROSPECT_ID),
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
    @DisplayName("getTODOs: prospect found - Failure")
    void getTODOsWhenProspectNotFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODOs(PROSPECT_ID),
                "getTodos must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getTODO: prospect and todo found - Success")
    void getTODOWhenProspectAndTODOFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    Todo todo = getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                    todo.setAssigneeId(PROSPECT_ID);
                    return todo;
                }).collect(Collectors.toList());
        prospectEntity.setTodos(todoList);
        int TODO_IDX = new Random().nextInt(3);
        String TARGET_TODO_ID = todoList.get(TODO_IDX).getId();

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Execute the service call
        TodoDTO returnedDTO = getService().getTODO(PROSPECT_ID, TARGET_TODO_ID);

        // Assertions
        Assertions.assertAll(String.format("Prospect with id [%s] contains the searched todo", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Todo can not be null"),
                () -> Assertions.assertEquals(
                        TARGET_TODO_ID,
                        returnedDTO.getId(),
                        String.format("Todo id must be [%s]", TARGET_TODO_ID)));
    }

    @Test
    @DisplayName("getTODO: prospect not found - Success")
    void getTODOWhenProspectNotFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODO(PROSPECT_ID, TODO_ID),
                "getTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getTODO: prospect found and todo not found - Success")
    void getTODOWhenProspectFoundAndTODONotFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        int TODO_LIST_SIZE = 3;
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
        List<Todo> todoList = IntStream
                .range(0, TODO_LIST_SIZE)
                .mapToObj( (i) -> {
                    String TODO_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getTodoMockFactory().newEntity(TODO_ID);
                }).collect(Collectors.toList());
        prospectEntity.setTodos(todoList);
        String TARGET_TODO_ID = getMockFactory().getTodoMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());
        doReturn(new TodoMapper()).when(todoService).getMapper();

        // Asserts
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getTODO(PROSPECT_ID, TARGET_TODO_ID),
                "getTODO must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("searchBy: search criteria entity null - Failure")
    void searchByWhenSearchInvalidFoundReturnsFailure() {
        // Execute and assert
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.searchBy(null),
                "searchBy must throw an ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
    }

    @Test
    @DisplayName("searchBy: search entity not null and entities found - Success")
    void searchByWhenSearchNotNullAndEntitiesFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPONENT_ID_1 = getMockFactory().getFAKER().internet().uuid();
        String COMPONENT_ID_2 = getMockFactory().getFAKER().internet().uuid();
        List<String> componentIds = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);
        TodoSearch search = TodoSearch.builder()
                .componentIds(componentIds)
                .build();
        int TODO_SIZE = 4;
        List<TodoDTO> todos = IntStream.range(0, TODO_SIZE).mapToObj( (idx) -> {
            String TODO_ID = getMockFactory().getComponentId();
            TodoDTO todo = getMockFactory().getTodoMockFactory().newDTO(TODO_ID);
            int componentIndex = getMockFactory().getFAKER().number().numberBetween(0, 2);
            String componentId = componentIds.get(componentIndex);
            todo.setComponentId(componentId);
            return todo;
        }).collect(Collectors.toList());
        List<Prospect> prospects = Arrays.asList(getMockFactory().newEntity(COMPONENT_ID_1), getMockFactory().newEntity(COMPONENT_ID_2));


        // Set up the mocked repository
        doReturn(todos).when(todoService).searchBy(any(TodoSearch.class));
        doReturn(prospects).when(repository).findAllByIdIn(componentIds);

        // Execute the service call
        List<ProspectTodoDTO> entityList = getService().searchBy(search);

        // Assertions
        Assertions.assertAll("List of TODOs must contain results",
                () -> Assertions.assertFalse(entityList.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, entityList.size(), String.format("Todo list size must be [%d]", TODO_SIZE)));
    }
}
