package com.sawoo.pipeline.api.integration.interaction;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.InteractionMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
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
        value = "interaction-integration-test-data.json",
        classType = Interaction.class,
        collectionNames = {DBConstants.INTERACTION_DOCUMENT})
public class InteractionIntegrationTest extends BaseIntegrationTest<InteractionDTO, Interaction, InteractionMockFactory> {

    private static final String INTERACTION_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "interaction-integration-expected-results.json";

    @Autowired
    public InteractionIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, InteractionMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.INTERACTION_CONTROLLER_API_BASE_URI,
                DBConstants.INTERACTION_DOCUMENT,
                INTERACTION_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Interaction> getClazz() {
        return Interaction.class;
    }
}
