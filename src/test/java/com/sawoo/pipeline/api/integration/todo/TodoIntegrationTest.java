package com.sawoo.pipeline.api.integration.todo;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@MongoDataFile(
        value = "todo-integration-test-data.json",
        classType = Todo.class,
        collectionNames = {DBConstants.TODO_DOCUMENT})
public class TodoIntegrationTest extends BaseIntegrationTest<TodoDTO, Todo, TodoMockFactory> {

    private static final String TEST_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "todo-integration-expected-results.json";

    @Autowired
    public TodoIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, TodoMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.TODO_CONTROLLER_API_BASE_URI,
                DBConstants.TODO_DOCUMENT,
                TEST_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Todo> getClazz() {
        return Todo.class;
    }
}
