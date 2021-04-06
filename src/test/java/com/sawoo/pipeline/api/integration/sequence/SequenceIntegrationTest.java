package com.sawoo.pipeline.api.integration.sequence;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@MongoDataFile(
        value = "sequence-integration-test-data.json",
        classType = Sequence.class,
        collectionNames = {DBConstants.SEQUENCE_DOCUMENT})
class SequenceIntegrationTest extends BaseIntegrationTest<SequenceDTO, Sequence, SequenceMockFactory> {

    private static final String TEST_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "sequence-integration-expected-results.json";

    @Autowired
    public SequenceIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, SequenceMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI,
                DBConstants.SEQUENCE_DOCUMENT,
                TEST_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Sequence> getClazz() {
        return Sequence.class;
    }

    @Test
    @Order(10)
    @DisplayName("GET /api/sequences/accounts/{accountIds}/main: findByAccountIds - Success")
    void findByAccountIdsWhenEntityFoundReturnsSuccess() throws Exception {
        // Set up
        String ACCOUNT_ID = "6030d6600c296a3a3c071293";

        // Execute the GET request
        getMockMvc().perform(get(getResourceURI() +
                "/" +
                ControllerConstants.ACCOUNT_CONTROLLER_RESOURCE_NAME +
                "/{accountIds}/main",
                ACCOUNT_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/sequences/accounts/main: findByAccountIds when accountIds null - Success")
    void findByAccountIdsWhenAccountIdsNotInformedReturnsSuccess() throws Exception {
        // Set up
        String ACCOUNT_ID_1 = "6030d65af796188aabff390b";
        String ACCOUNT_ID_2 = "6030d6600c296a3a3c071293";
        String ACCOUNT_IDS = String.join(",", Arrays.asList(ACCOUNT_ID_1, ACCOUNT_ID_2));

        // Execute the GET request
        getMockMvc().perform(get(getResourceURI() +
                "/" +
                ControllerConstants.ACCOUNT_CONTROLLER_RESOURCE_NAME +
                "/{accountIds}/main",
                ACCOUNT_IDS))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
