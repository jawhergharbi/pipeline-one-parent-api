package com.sawoo.pipeline.api.integration.user;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.User;
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
        value = "user-integration-test-data.json",
        classType = User.class,
        collectionNames = {DBConstants.USER_DOCUMENT})
public class UserIntegrationTest extends BaseIntegrationTest<UserAuthDTO, User, UserMockFactory> {

    private static final String USER_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "user-integration-expected-results.json";

    @Autowired
    public UserIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, UserMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.USER_CONTROLLER_API_BASE_URI,
                DBConstants.USER_DOCUMENT,
                USER_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<User> getClazz() {
        return User.class;
    }
}
