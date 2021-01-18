package com.sawoo.pipeline.api.repository.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.model.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class AccountLeadRepositoryTest {

    private static final File ACCOUNT_LEAD_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "account-lead-test-data.json").toFile();
    private static final String ACCOUNT_ID_NO_LEADS = "5fce6a364d9da6645ba36";
    private static final String ACCOUNT_ID = "3ee4e66da4d966de664af";

    private final AccountRepository repository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountLeadRepositoryTest(AccountRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    protected void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Account[] entityArray = mapper.readValue(ACCOUNT_LEAD_JSON_DATA, Account[].class);

        List<Account> entityList = Arrays.asList(entityArray);

        List<User> users = entityList.stream().flatMap(a -> a.getUsers().stream()).distinct().collect(Collectors.toList());
        mongoTemplate.insertAll(users);

        List<Lead> leads = entityList.stream().flatMap(a -> a.getLeads().stream()).distinct().collect(Collectors.toList());
        mongoTemplate.insertAll(leads);

        // Load each entity into the DB
        repository.insert(entityList);
    }

    @AfterEach
    protected void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
        mongoTemplate.dropCollection(User.class);
        mongoTemplate.dropCollection(Company.class);
        mongoTemplate.dropCollection(Person.class);
        mongoTemplate.dropCollection(Lead.class);
    }

    @Test
    @DisplayName("findAllLeads: entity found - Success")
    void findAllLeadsWhenEntityFoundReturnsSuccess() {
        Optional<Account> account = repository.findById(ACCOUNT_ID);

        Assertions.assertAll(String.format("Account id [%s] must have [%d] lead/s", ACCOUNT_ID, 1),
                () -> Assertions.assertTrue(account.isPresent(), String.format("Account id [%s] must exist", ACCOUNT_ID)),
                () -> account.ifPresent( a -> Assertions.assertFalse(
                        a.getLeads().isEmpty(),
                        String.format("Lead list for account id [%s] can not be empty", ACCOUNT_ID))),
                () -> account.ifPresent( a -> Assertions.assertEquals(
                        1,
                        a.getLeads().size(),
                        String.format("Lead list for account id [%s] must be [%d]", ACCOUNT_ID, 1))));
    }

    @Test
    @DisplayName("findAllLeads: entity found but no leads for the account - Success")
    void findAllLeadsWhenEntityFoundAndNoLeadsReturnsSuccess() {
        Optional<Account> account = repository.findById(ACCOUNT_ID_NO_LEADS);

        Assertions.assertAll(String.format("Account id [%s] must have no lead/s", ACCOUNT_ID),
                () -> Assertions.assertTrue(account.isPresent(), String.format("Account id [%s] must exist", ACCOUNT_ID)),
                () -> Assertions.assertTrue(
                        account.isPresent() && account.get().getLeads().isEmpty(),
                        String.format("Lead list for account id [%s] must be empty", ACCOUNT_ID))
        );
    }

    @Test
    @DisplayName("findAllLeads: entity not found - Failure")
    void findAllLeadsWhenEntityNotFoundReturnsFailure() {
        String ACCOUNT_ID = "wrong_key";
        Optional<Account> account = repository.findById(ACCOUNT_ID);

        Assertions.assertFalse(account.isPresent(), String.format("Account id [%s] can not be present", ACCOUNT_ID));
    }
}
