package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

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
        String PROSPECT_TODO_ID = getMockFactory().getComponentId();
        TodoDTO mockedDTO = getMockFactory().newDTO(null);
        Todo todo = getMockFactory().newEntity(PROSPECT_TODO_ID);

        // Set up the mocked repository
        doReturn(todo).when(repository).insert(any(Todo.class));

        // Execute the service call
        TodoDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating prospect todo with id [[%s] must return the proper entity", PROSPECT_TODO_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        PROSPECT_TODO_ID,
                        returnedEntity.getId(),
                        String.format("Prospect todo id must be [%s]", PROSPECT_TODO_ID)));

        verify(repository, never()).findById(anyString());
        verify(repository, times(1)).insert(any(Todo.class));
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
}
