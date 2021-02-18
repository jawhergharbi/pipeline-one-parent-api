package com.sawoo.pipeline.api.integration.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.integration.MongoCleanUp;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@MongoDataFile(
        value = "account-integration-test-data.json",
        classType = Account.class,
        collectionNames = { DBConstants.ACCOUNT_DOCUMENT, DBConstants.COMPANY_DOCUMENT })
public class AccountIntegrationTest extends BaseIntegrationTest<AccountDTO, Account, AccountMockFactory> {

    private static final String ACCOUNT_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "account-integration-expected-results.json";

    @Autowired
    public AccountIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, AccountMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI,
                DBConstants.ACCOUNT_DOCUMENT,
                ACCOUNT_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Account> getClazz() {
        return Account.class;
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/accounts: create account must also create the user - Success")
    @MongoCleanUp(collectionNames = {DBConstants.ACCOUNT_DOCUMENT, DBConstants.USER_DOCUMENT, DBConstants.COMPANY_DOCUMENT})
    void createWhenEntityNotFoundReturnsSuccess() throws Exception {
        // Set up the mock entities
        AccountDTO postEntity = getMockFactory().newDTO(null);
        String EMAIL = postEntity.getEmail();
        String COMPANY_NAME = postEntity.getCompany().getName();

        // Execute the POST request
        getMockMvc().perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.created").exists());

        // Assertions
        Query query = Query.query(Criteria.where("email").is(EMAIL));
        Account account = getMongoTemplate().findOne(query, Account.class);
        Assertions.assertAll("Assert account entity has been properly stored",
                () -> Assertions.assertNotNull(
                        account,
                        String.format("Account not found with email: [%s]", EMAIL)),
                () -> Assertions.assertNotNull(
                        account.getCompany(),
                        String.format("Company can not be null for account with email: [%s]", EMAIL)),
                () -> Assertions.assertEquals(
                        COMPANY_NAME,
                        account.getCompany().getName(),
                        String.format("Company name must be [%s]", COMPANY_NAME)),
                () -> Assertions.assertFalse(
                        account.getUsers().isEmpty(),
                        "List of users can not be empty"
                ));
    }

    @Test
    @Order(11)
    @DisplayName("POST /api/accounts: create account when user is already created - Failure")
    @MongoCleanUp(collectionNames = {DBConstants.ACCOUNT_DOCUMENT, DBConstants.USER_DOCUMENT, DBConstants.COMPANY_DOCUMENT})
    void createWhenEntityNotFoundButUserFoundReturnsFailure() throws Exception {
        // Set up the mock entities
        AccountDTO postEntity = getMockFactory().newDTO(null);
        String EMAIL = postEntity.getEmail();
        User user = getMockFactory().getUserMockFactory().newEntity(null);
        user.setFullName(postEntity.getFullName());
        user.setEmail(EMAIL);
        getMongoTemplate().save(user);

        // Execute the POST request
        getMockMvc().perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message",
                        stringContainsInOrder("Entity type [user]", "already exits in the system")));

        Query query = Query.query(Criteria.where("email").is(EMAIL));
        Account account = getMongoTemplate().findOne(query, Account.class);
        Assertions.assertNull(account, String.format("Account with email: [%s] must be null", EMAIL));
    }
}
