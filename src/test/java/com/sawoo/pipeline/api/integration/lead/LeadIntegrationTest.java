package com.sawoo.pipeline.api.integration.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoDataFileList;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.MongoTestFile;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.lead.Lead;
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
        value = "lead-integration-base-test-data.json",
        classType = Lead.class,
        collectionNames = {DBConstants.LEAD_DOCUMENT})
@MongoDataFileList(
        files = {
                @MongoTestFile(fileName = "lead-interaction-integration-test-data.json", classType = Interaction.class),
                @MongoTestFile(fileName = "lead-integration-test-data.json", classType = Lead.class)},
        collectionNames = {
                DBConstants.LEAD_DOCUMENT,
                DBConstants.PERSON_DOCUMENT,
                DBConstants.COMPANY_DOCUMENT,
                DBConstants.INTERACTION_DOCUMENT})
public class LeadIntegrationTest extends BaseIntegrationTest<LeadDTO, Lead, LeadMockFactory> {

    private static final String LEAD_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "lead-integration-expected-results.json";

    @Autowired
    public LeadIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, LeadMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                LEAD_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Lead> getClazz() {
        return Lead.class;
    }

    @Test
    @Order(10)
    @DisplayName("GET /api/leads/{id}/interactions: get interactions for a lead - Success")
    void getInteractionsWhenEntityNotFoundReturnsSuccess() throws Exception {
        String LEAD_ID = "601c2aa7cb7a517712ad6be3";
        int INTERACTION_LIST_SIZE = 3;

        // Execute the GET request
        getMockMvc().perform(get(getResourceURI() + "/{id}/interactions", LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(INTERACTION_LIST_SIZE)));
    }
}
