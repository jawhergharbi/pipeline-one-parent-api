package com.sawoo.pipeline.api.integration.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
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

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@MongoDataFile(
        value = "account-integration-test-data.json",
        classType = Account.class,
        collectionName = DBConstants.ACCOUNT_DOCUMENT)
public class AccountIntegrationTest extends BaseIntegrationTest<Account> {

    private static final String ACCOUNT_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "account-integration-expected-results.json";

    @Autowired
    public AccountIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI,
                DBConstants.ACCOUNT_DOCUMENT,
                ACCOUNT_INTEGRATION_EXPECTED_RESULTS_FILE_NAME);
    }

    @Override
    protected Class<Account> getClazz() {
        return Account.class;
    }
}
