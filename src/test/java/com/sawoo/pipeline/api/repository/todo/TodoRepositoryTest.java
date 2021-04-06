package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class TodoRepositoryTest extends BaseRepositoryTest<Todo, TodoRepository, TodoMockFactory> {

    private static final String TEST_JSON_DATA_FILE_NAME = "todo-test-data.json";
    private static final String ENTITY_ID = "60647db4559c7e603cd1a0f5";
    private static final String COMPONENT_ID_1 = "5fa3c963da6ra335fa2s323d45b";
    private static final String COMPONENT_ID_2 = "a335f5236ra2fda33s323c96d45b";


    @Autowired
    public TodoRepositoryTest(
            TodoRepository repository,
            TodoMockFactory mockFactory) {
        super(repository, TEST_JSON_DATA_FILE_NAME, ENTITY_ID, Todo.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Todo[]> getClazz() {
        return Todo[].class;
    }

    @Override
    protected String getComponentId(Todo component) {
        return component.getId();
    }

    @Override
    protected Todo getNewEntity() {
        String PROSPECT_TODO_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(PROSPECT_TODO_ID);
    }

    @Test
    @DisplayName("findByComponentId: entities found - Success")
    void findByComponentIdWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 3;

        // Execute query
        List<Todo> todos = getRepository().findByComponentId(COMPONENT_ID_1);

        // Assertions
        Assertions.assertAll(String.format("TODOs for prospect with [%s]", COMPONENT_ID_1),
                () -> Assertions.assertFalse(todos.isEmpty(), "List of todos can not be null"),
                () -> Assertions.assertEquals(
                        ENTITY_SIZE,
                        todos.size(),
                        String.format("TODOs with prospect id [%s] must be [%d]", COMPONENT_ID_1, ENTITY_SIZE)),
                () -> Assertions.assertEquals(
                        COMPONENT_ID_1,
                        todos.get(0).getComponentId(),
                        String.format("Prospect id must be [%s]", COMPONENT_ID_1)));
    }

    @Test
    @DisplayName("findByComponentIdIn: entities found - Success")
    void findByComponentIdInWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 7;
        List<String> COMPONENTS_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByComponentIdIn(COMPONENTS_IDS);

        // Assertions
        Assertions.assertAll(String.format("TODOs for prospect with ids [%s]", COMPONENTS_IDS.toArray()),
                () -> Assertions.assertFalse(todos.isEmpty(), "List of todos can not be null"),
                () -> Assertions.assertEquals(
                        ENTITY_SIZE,
                        todos.size(),
                        String.format("TODOs with prospect id [%s] must be [%d]", COMPONENT_ID_1, ENTITY_SIZE)));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByWhenStatusTypeAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 1;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<Todo> todos = getRepository().findBy(STATUS, TYPE, Collections.singletonList(COMPONENT_ID_1));

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByWhenStatusTypeAndComponentIdsEmptyAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 2;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<Todo> todos = getRepository().findBy(STATUS, TYPE, Collections.emptyList());

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByWhenStatusTypeAndComponentIdsNullAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 2;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<Todo> todos = getRepository().findBy(STATUS, TYPE, null);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByWhenStatusListTypeListAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 7;
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findBy(null, null, COMPONENT_IDS);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByStatusAndTypeWhenStatusListNullTypeListNullAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 7;
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByStatusAndType(null, null, COMPONENT_IDS);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByStatusAndTypeWhenStatusListTypeListAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 5;
        List<Integer> types = Collections.singletonList(1);
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByStatusAndType(null, types, COMPONENT_IDS);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findByAssigneeId: filter todos by userId - Success")
    void findByAssigneeIdWhenUserIdDoesExistReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 5;
        String ASSIGNEE_ID = "5fa317cd0efe4d20ad3edd13";

        // Execute query
        List<Todo> todos = getRepository().findByAssigneeId(ASSIGNEE_ID);

        Assertions.assertAll(String.format("TODOs filter by userId [%s]", ASSIGNEE_ID),
                () -> Assertions.assertFalse(
                        todos.isEmpty(),
                        "TODOs can not be empty"),
                () -> Assertions.assertEquals(
                        ENTITY_SIZE,
                        todos.size(),
                        String.format("TODOs size must be [%d]", ENTITY_SIZE)));
    }

    @Test
    @DisplayName("findByAssigneeId: filter todos by userId not todos found- Success")
    void findByAssigneeIdWhenUserIdDoesNotExistReturnsSuccess() {
        // Assign
        String ASSIGNEE_ID = "wrongId";

        // Execute query
        List<Todo> todos = getRepository().findByAssigneeId(ASSIGNEE_ID);

        Assertions.assertAll(String.format("TODOs filter by assigneeId [%s]", ASSIGNEE_ID),
                () -> Assertions.assertTrue(
                        todos.isEmpty(),
                        "TODOs must be empty"));
    }
}
