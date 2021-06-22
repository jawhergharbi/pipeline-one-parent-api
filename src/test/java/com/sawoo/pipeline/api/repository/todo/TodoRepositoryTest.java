package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
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
        int ENTITY_SIZE = 8;
        List<String> COMPONENTS_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByComponentIdIn(COMPONENTS_IDS);

        // Assertions
        Assertions.assertAll(String.format("TODOs for componentIds with ids [%s]", COMPONENTS_IDS.toArray()),
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
        int CHANNEL = 0;

        // Execute query
        List<Todo> todos = getRepository().findBy(STATUS, CHANNEL, null);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, componentIds and entities found - Success")
    void findByWhenStatusListTypeListAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 8;
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
        int ENTITY_SIZE = 8;
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByStatusAndChannel(null, null, COMPONENT_IDS);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, channel, componentIds and entities found - Success")
    void findByStatusAndTypeWhenStatusListChannelListAndComponentIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 6;
        List<Integer> channels = Collections.singletonList(1);
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Todo> todos = getRepository().findByStatusAndChannel(null, channels, COMPONENT_IDS);

        Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty");
        Assertions.assertEquals(ENTITY_SIZE, todos.size(), String.format("TODOs size must be [%d]", ENTITY_SIZE));
    }

    @Test
    @DisplayName("findByAssigneeId: filter todos by userId - Success")
    void findByAssigneeIdWhenUserIdDoesExistReturnsSuccess() {
        // Assign
        int ENTITY_SIZE = 6;
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

    @Test
    @DisplayName("searchBy: search by status and sourceId")
    void searchByWhenStatusAndSourceIdReturnsSuccess() {
        // Assign
        int TODO_SIZE = 2;
        String SOURCE_ID = "60647e3d631a80a71795ff03";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        List<Todo> todos = getRepository().searchBy(searchBy);

        Assertions.assertAll(String.format("TODOs filter by search criteria [%s]", searchBy),
                () -> Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, todos.size(), String.format("TODOs size must be [%d]", TODO_SIZE)));
    }

    @Test
    @DisplayName("searchBy: search by status, sourceId and sourceType")
    void searchByWhenStatusAndSourceIdAndSourceTypeReturnsSuccess() {
        // Assign
        String SOURCE_ID = "wrong_source_id";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceType(Collections.singletonList(TodoSourceType.AUTOMATIC.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        List<Todo> todos = getRepository().searchBy(searchBy);

        Assertions.assertTrue(todos.isEmpty(), "TODOs must be empty");
    }

    @Test
    @DisplayName("searchBy: no entities fit the search criteria")
    void searchByWhenNoEntitiesFitSearchCriteriaReturnsSuccess() {
        // Assign
        int TODO_SIZE = 1;
        String SOURCE_ID = "60647e3d631a80a71795ff03";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceType(Collections.singletonList(TodoSourceType.AUTOMATIC.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        List<Todo> todos = getRepository().searchBy(searchBy);

        Assertions.assertAll(String.format("TODOs filter by search criteria [%s]", searchBy),
                () -> Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, todos.size(), String.format("TODOs size must be [%d]", TODO_SIZE)));
    }

    @Test
    @DisplayName("remove: remove by status and sourceId")
    void removeWhenStatusAndSourceIdReturnsSuccess() {
        // Assign
        int TODO_SIZE = 2;
        String SOURCE_ID = "60647e3d631a80a71795ff03";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        long deleted = getRepository().remove(searchBy);

        // Assertions
        Assertions.assertEquals(TODO_SIZE, deleted, String.format("TODOs deleted must be [%d]", TODO_SIZE));
    }

    @Test
    @DisplayName("remove: remove by status, sourceId and sourceType")
    void removeWhenStatusAndSourceIdAndSourceTypeReturnsSuccess() {
        // Assign
        int TODO_SIZE = 1;
        String SOURCE_ID = "60647e3d631a80a71795ff03";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceType(Collections.singletonList(TodoSourceType.AUTOMATIC.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        long deleted = getRepository().remove(searchBy);

        // Assertions
        Assertions.assertEquals(TODO_SIZE, deleted, String.format("TODOs deleted must be [%d]", TODO_SIZE));
    }

    @Test
    @DisplayName("findAllAndRemove: remove by status, sourceId and sourceType")
    void findAllAndRemoveWhenStatusAndSourceIdAndSourceTypeReturnsSuccess() {
        // Assign
        int TODO_SIZE = 1;
        int REMAINING_ENTITIES_SIZE = getDocumentSize() - TODO_SIZE;
        String SOURCE_ID = "60647e3d631a80a71795ff03";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceType(Collections.singletonList(TodoSourceType.AUTOMATIC.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        List<Todo> entitiesDeleted = getRepository().findAllAndRemove(searchBy);
        List<Todo> remainingEntities = getRepository().findAll();

        // Assertions
        Assertions.assertFalse(entitiesDeleted.isEmpty(), "Deleted entities list can not be empty");
        Assertions.assertEquals(TODO_SIZE, entitiesDeleted.size(), String.format("Number of TODOs deleted must be [%d]", TODO_SIZE));
        Assertions.assertFalse(remainingEntities.isEmpty(), "Remaining entities list can not be empty");
        Assertions.assertEquals(REMAINING_ENTITIES_SIZE, remainingEntities.size(), String.format("Number of remaining TODOs must be [%d]", REMAINING_ENTITIES_SIZE));
    }

    @Test
    @DisplayName("remove: no entities fit the search criteria")
    void removeWhenNoEntitiesFitSearchCriteriaReturnsSuccess() {
        // Assign
        int TODO_SIZE = 0;
        String SOURCE_ID = "wrong_source_id";
        TodoSearch searchBy = TodoSearch.builder()
                .status(Arrays.asList(TodoStatus.PENDING.getValue(), TodoStatus.CANCELLED.getValue()))
                .sourceType(Collections.singletonList(TodoSourceType.AUTOMATIC.getValue()))
                .sourceId(Collections.singletonList(SOURCE_ID))
                .build();

        // Execute query
        long deleted = getRepository().remove(searchBy);

        // Assertions
        Assertions.assertEquals(TODO_SIZE, deleted, String.format("TODOs deleted must be [%d]", TODO_SIZE));
    }

    @Test
    @DisplayName("searchBy: campaign id and neither done not cancelled")
    void searchByWhenCampaignAnyStatusExceptDoneAndCancelledReturnsSuccess() {
        // Assign
        int TODO_SIZE = 1;
        String CAMPAIGN_ID = "60488a88959ceb1ce1e518fa";
        TodoSearch searchBy = TodoSearch.builder()
                .campaignIds(Collections.singletonList(CAMPAIGN_ID))
                .status(Arrays.asList(
                        TodoStatus.PENDING.getValue(),
                        TodoStatus.ON_GOING.getValue(),
                        TodoStatus.UNASSIGNED.getValue()))
                .build();

        // Execute query
        List<Todo> todos = getRepository().searchBy(searchBy);

        Assertions.assertAll(String.format("TODOs filter by search criteria [%s]", searchBy),
                () -> Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, todos.size(), String.format("TODOs size must be [%d]", TODO_SIZE)));
    }

    @Test
    @DisplayName("searchBy: campaign id")
    void searchByWhenCampaignReturnsSuccess() {
        // Assign
        int TODO_SIZE = 2;
        String CAMPAIGN_ID = "60488a88959ceb1ce1e518fa";
        TodoSearch searchBy = TodoSearch.builder()
                .campaignIds(Collections.singletonList(CAMPAIGN_ID))
                .build();

        // Execute query
        List<Todo> todos = getRepository().searchBy(searchBy);

        Assertions.assertAll(String.format("TODOs filter by search criteria [%s]", searchBy),
                () -> Assertions.assertFalse(todos.isEmpty(), "TODOs can not be empty"),
                () -> Assertions.assertEquals(TODO_SIZE, todos.size(), String.format("TODOs size must be [%d]", TODO_SIZE)));
    }
}
