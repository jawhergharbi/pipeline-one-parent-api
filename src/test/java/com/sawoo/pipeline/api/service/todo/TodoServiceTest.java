package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoSearchDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
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
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoServiceTest extends BaseServiceTest<TodoDTO, Todo, TodoRepository, TodoService, TodoMockFactory> {

    @MockBean
    private TodoRepository repository;

    @MockBean
    private TodoServiceEventListener serviceEventListener;

    @Autowired
    public TodoServiceTest(TodoMockFactory mockFactory, TodoService service) {
        super(mockFactory, DBConstants.TODO_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Todo component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(TodoDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Todo entity) {
        doReturn(Optional.of(entity)).when(repository).findById(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String TODO_ID = getMockFactory().getComponentId();
        TodoDTO mockedDTO = getMockFactory().newDTO(null);
        Todo todo = getMockFactory().newEntity(TODO_ID);

        // Set up the mocked repository
        doReturn(todo).when(repository).insert(any(Todo.class));

        // Execute the service call
        TodoDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating prospect todo with id [[%s] must return the proper entity", TODO_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedEntity.getId(),
                        String.format("Prospect todo id must be [%s]", TODO_ID)));

        verify(repository, never()).findById(anyString());
        verify(repository, times(1)).insert(any(Todo.class));
        verify(serviceEventListener, times(1)).handleBeforeInsertEvent(any(BaseServiceBeforeInsertEvent.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_TODO_ID = getMockFactory().getComponentId();
        TodoDTO mockedDTOTOUpdate = new TodoDTO();
        mockedDTOTOUpdate.setScheduled(LocalDateTime.now(ZoneOffset.UTC));
        Todo todoEntity = getMockFactory().newEntity(PROSPECT_TODO_ID);

        // Set up the mocked repository
        doReturn(Optional.of(todoEntity)).when(repository).findById(PROSPECT_TODO_ID);

        // Execute the service call
        TodoDTO returnedDTO = getService().update(PROSPECT_TODO_ID, mockedDTOTOUpdate);

        Assertions.assertAll(String.format("Prospect todo entity with id [%s] must be properly updated", PROSPECT_TODO_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Prospect todo entity can not be null"),
                () -> Assertions.assertEquals(
                        LocalDateTime.now(ZoneOffset.UTC).getDayOfMonth(),
                        returnedDTO.getScheduled().getDayOfMonth(),
                        "Scheduled date must be today"));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("create: when entity does not exist but status invalid - Failure")
    void createWhenEntityDoesNotExistAndStatusInvalidReturnsFailure() {
        // Set up mocked entities
        TodoDTO mockedDTO = getMockFactory().newDTO(null);
        mockedDTO.setStatus(1000);

        // Act / Assert
        TodoService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () ->  service.create(mockedDTO),
                "create must throw a ConstraintViolationException");

        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_ILLEGAL_ENUMERATION_VALUE_EXCEPTION)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).insert(any(Todo.class));
    }

    @Test
    @DisplayName("create: when entity does not exist and status must be pending - Failure")
    void createWhenEntityDoesNotExistAndStatusInvalidReturnsSuccess() {
        // Set up mocked entities
        String TODO_ID = getMockFactory().getComponentId();
        TodoDTO mockedDTO = getMockFactory().newDTO(null);
        mockedDTO.setStatus(null);
        Todo todo = getMockFactory().newEntity(TODO_ID);

        // Set up the mocked repository
        doReturn(todo).when(repository).insert(any(Todo.class));

        // Execute the service call
        TodoDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating prospect todo with id [[%s] must return the proper entity", TODO_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        TODO_ID,
                        returnedEntity.getId(),
                        String.format("Prospect todo id must be [%s]", TODO_ID)));

        verify(repository, times(1)).insert(any(Todo.class));
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
        List<Todo> todos = IntStream.range(0, TODO_SIZE).mapToObj( (idx) -> {
            String TODO_ID = getMockFactory().getComponentId();
            return getMockFactory().newEntity(TODO_ID);
        }).collect(Collectors.toList());


        // Set up the mocked repository
        doReturn(todos).when(repository).searchBy(any(TodoSearch.class));

        // Execute the service call
        List<TodoDTO> entityList = getService().searchBy(search);

        // Assertions
        Assertions.assertAll("List of TODOs must contain results",
                () -> Assertions.assertFalse(entityList.isEmpty(), "Todo list can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, entityList.size(), String.format("Todo list size must be [%d]", TODO_SIZE)));
    }

    @Test
    @DisplayName("searchBy: search criteria entity null - Failure")
    void searchByWhenSearchInvalidFoundReturnsFailure() {
        // Execute and assert
        TodoService service = getService();
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
    @DisplayName("remove: search entity not null and entities found - Success")
    void removeWhenSearchNotNullAndEntitiesFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPONENT_ID_1 = getMockFactory().getFAKER().internet().uuid();
        String COMPONENT_ID_2 = getMockFactory().getFAKER().internet().uuid();
        List<String> componentIds = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);
        TodoSearchDTO search = TodoSearchDTO.builder()
                .componentIds(componentIds)
                .build();
        long TODO_SIZE = 4;

        // Set up the mocked repository
        doReturn(TODO_SIZE).when(repository).remove(any(TodoSearch.class));

        // Execute the service call
        long deleted = getService().remove(search);

        // Assertions
        Assertions.assertEquals(TODO_SIZE, deleted, String.format("Todo list size must be [%d]", TODO_SIZE));
    }

    @Test
    @DisplayName("remove: search criteria entity mot null but componentIds empty - Failure")
    void removeWhenSearchInvalidComponentIdsNullFoundReturnsFailure() {
        TodoSearchDTO search = TodoSearchDTO.builder()
                .componentIds(Collections.emptyList())
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.ON_GOING.getValue()))
                .build();

        // Execute and assert
        TodoService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.remove(search),
                "remove must throw an ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exception.getMessage()));
    }

    @Test
    @DisplayName("remove: search criteria entity null - Failure")
    void removeWhenSearchInvalidFoundReturnsFailure() {
        // Execute and assert
        TodoService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.remove(null),
                "searchBy must throw an ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
    }
}
