package com.sawoo.pipeline.api.integration.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestMethodOrder;
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
        value = "lead-integration-test-data.json",
        classType = Lead.class,
        collectionNames = {DBConstants.LEAD_DOCUMENT})
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
}
