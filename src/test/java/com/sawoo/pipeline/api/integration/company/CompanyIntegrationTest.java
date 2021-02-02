package com.sawoo.pipeline.api.integration.company;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.company.Company;
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
        value = "company-integration-test-data.json",
        classType = Company.class,
        collectionNames = {DBConstants.COMPANY_DOCUMENT})
public class CompanyIntegrationTest extends BaseIntegrationTest<CompanyDTO, Company, CompanyMockFactory> {

    private static final String COMPANY_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "company-integration-expected-results.json";

    @Autowired
    public CompanyIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, CompanyMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.COMPANY_CONTROLLER_API_BASE_URI,
                DBConstants.COMPANY_DOCUMENT,
                COMPANY_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Company> getClazz() {
        return Company.class;
    }
}
