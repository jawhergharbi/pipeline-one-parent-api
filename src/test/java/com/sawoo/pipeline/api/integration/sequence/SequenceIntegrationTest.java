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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
public class SequenceIntegrationTest extends BaseIntegrationTest<SequenceDTO, Sequence, SequenceMockFactory> {

    private static final String SEQUENCE_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "sequence-integration-expected-results.json";

    @Autowired
    public SequenceIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, SequenceMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI,
                DBConstants.SEQUENCE_DOCUMENT,
                SEQUENCE_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
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
        // Set up the mock entities
        String ACCOUNT_ID = "6027a3436fb12b99f63b0e23";
        Set<String> accountIds = new HashSet<>(Collections.singletonList(ACCOUNT_ID));

        // Execute the GET request
        getMockMvc().perform(get(getResourceURI() + "/accounts/{accountIds}/main", accountIds))

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
        // Execute the GET request
        getMockMvc().perform(get(getResourceURI() + "/accounts/main"))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
