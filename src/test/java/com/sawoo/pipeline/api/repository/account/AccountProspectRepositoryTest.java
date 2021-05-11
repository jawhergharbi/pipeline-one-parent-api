package com.sawoo.pipeline.api.repository.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
class AccountProspectRepositoryTest {

    private static final File TEST_ACCOUNT_PROSPECT_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "account-prospect-test-data.json").toFile();
    private static final String ACCOUNT_ID_NO_PROSPECTS = "5fce6a364d9da6645ba36";
    private static final String ACCOUNT_ID = "3ee4e66da4d966de664af";

    private final AccountRepository repository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountProspectRepositoryTest(AccountRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    protected void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Account[] entityArray = mapper.readValue(TEST_ACCOUNT_PROSPECT_JSON_DATA, Account[].class);

        List<Account> entityList = Arrays.asList(entityArray);

        List<User> users = entityList.stream().flatMap(a -> a.getUsers().stream()).distinct().collect(Collectors.toList());
        mongoTemplate.insertAll(users);

        List<Prospect> prospects = entityList.stream().flatMap(a -> a.getProspects().stream()).distinct().collect(Collectors.toList());
        mongoTemplate.insertAll(prospects);

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
        mongoTemplate.dropCollection(Prospect.class);
    }

    @Test
    @DisplayName("findAllProspects: entity found - Success")
    void findAllProspectsWhenEntityFoundReturnsSuccess() {
        Optional<Account> account = repository.findById(ACCOUNT_ID);

        Assertions.assertAll(String.format("Account id [%s] must have [%d] prospect/s", ACCOUNT_ID, 1),
                () -> Assertions.assertTrue(account.isPresent(), String.format("Account id [%s] must exist", ACCOUNT_ID)),
                () -> account.ifPresent( a -> Assertions.assertFalse(
                        a.getProspects().isEmpty(),
                        String.format("Prospect list for account id [%s] can not be empty", ACCOUNT_ID))),
                () -> account.ifPresent( a -> Assertions.assertEquals(
                        1,
                        a.getProspects().size(),
                        String.format("Prospect list for account id [%s] must be [%d]", ACCOUNT_ID, 1))));
    }

    @Test
    @DisplayName("findAllProspects: entity found but no prospect for the account - Success")
    void findAllProspectsWhenEntityFoundAndNoProspectReturnsSuccess() {
        Optional<Account> account = repository.findById(ACCOUNT_ID_NO_PROSPECTS);

        Assertions.assertAll(String.format("Account id [%s] must have no prospect/s", ACCOUNT_ID),
                () -> Assertions.assertTrue(account.isPresent(), String.format("Account id [%s] must exist", ACCOUNT_ID)),
                () -> Assertions.assertTrue(
                        account.isPresent() && account.get().getProspects().isEmpty(),
                        String.format("Prospect list for account id [%s] must be empty", ACCOUNT_ID))
        );
    }

    @Test
    @DisplayName("findAllProspects: entity not found - Failure")
    void findAllProspectsWhenEntityNotFoundReturnsFailure() {
        String ACCOUNT_ID = "wrong_key";
        Optional<Account> account = repository.findById(ACCOUNT_ID);

        Assertions.assertFalse(account.isPresent(), String.format("Account id [%s] can not be present", ACCOUNT_ID));
    }

    @Test
    @DisplayName("findByProspectId: entities found - Success")
    void findByProspectIdWhenEntitiesFoundReturnsSuccess() {
        String PROSPECT_ID = "601bbbf0f9589897c09cb668";
        Optional<Account> account = repository.findByProspectId(PROSPECT_ID);

        Assertions.assertAll(String.format("Accounts by prospectId [%s] must have [%d] entities", PROSPECT_ID, 1),
                () -> Assertions.assertTrue(account.isPresent(), "Account must be present"),
                () -> account.ifPresent(
                        (a) -> Assertions.assertTrue(a.getProspects().stream().anyMatch(l -> l.getId().equals(PROSPECT_ID)),
                                String.format("Prospect with id [%s] must be present]", PROSPECT_ID))));
    }
}
