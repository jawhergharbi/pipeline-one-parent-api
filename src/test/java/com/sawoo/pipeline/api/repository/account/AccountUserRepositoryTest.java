package com.sawoo.pipeline.api.repository.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.account.Account;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class AccountUserRepositoryTest {

    private static final File ACCOUNT_USER_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "account-user-test-data.json").toFile();
    private static final String ACCOUNT_ID_NO_USERS = "3eed964e66da46de664af";
    private static final String ACCOUNT_ID = "3ee4e66da4d966de664af";
    private static final String USER_ID = "5fa317cd0efe4d20ad3edd13";

    private final AccountRepository repository;
    private final AccountMockFactory mockFactory;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountUserRepositoryTest(AccountRepository repository, AccountMockFactory mockFactory, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mockFactory = mockFactory;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    protected void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Account[] entityArray = mapper.readValue(ACCOUNT_USER_JSON_DATA, Account[].class);

        List<Account> entityList = Arrays.asList(entityArray);

        List<User> users = entityList.stream().flatMap(a -> a.getUsers().stream()).distinct().collect(Collectors.toList());
        mongoTemplate.insertAll(users);

        // Load each entity into the DB
        repository.insert(entityList);
    }

    @AfterEach
    protected void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
        mongoTemplate.dropCollection(User.class);
        mongoTemplate.dropCollection(Company.class);
    }

    @Test
    @DisplayName("save: add user - Success")
    void saveWhenAddUserReturnsSuccess() {
        String USER_PASSWORD = mockFactory.getFAKER().internet().password(6, 12);
        String USER_EMAIL = mockFactory.getFAKER().internet().emailAddress();
        User user = mockFactory.getUserMockFactory().newEntity(null, USER_EMAIL, USER_PASSWORD, new String[]{UserRole.MNG.name()});
        mongoTemplate.save(user);
        long userDocSize = mongoTemplate.count(new Query(), DBConstants.USER_DOCUMENT);

        repository
                .findById(ACCOUNT_ID_NO_USERS)
                .ifPresent((account) -> {
                    account.getUsers().add(user);
                    repository.save(account);
                });

        Optional<Account> account = repository.findById(ACCOUNT_ID_NO_USERS);

        Assertions.assertEquals(
                userDocSize,
                mongoTemplate.count(new Query(), DBConstants.USER_DOCUMENT),
                String.format("User repository size has to be equal to [%d]", userDocSize));

        Assertions.assertTrue(account.isPresent(), "Account must be present");
        Assertions.assertAll("Saved user account validation",
                () -> Assertions.assertNotNull(account.get().getUsers(), "User list can not be null"),
                () -> Assertions.assertFalse(account.get().getUsers().isEmpty(), "User list can not be empty"),
                () -> {
                    User savedUser = account.get().getUsers().iterator().next();
                    Assertions.assertNotNull(savedUser.getId(), "User id can not be null");
                    Assertions.assertEquals(user.getId(), savedUser.getId(), String.format("User id must equal to [%s]", user.getId()));
                });
    }

    @Test
    @DisplayName("save: add twice the same user - Success")
    void saveWhenAddTwiceSameUserReturnsSuccess() {
        String USER_PASSWORD = mockFactory.getFAKER().internet().password(6, 12);
        String USER_EMAIL = mockFactory.getFAKER().internet().emailAddress();
        User user = mockFactory.getUserMockFactory().newEntity(null, USER_EMAIL, USER_PASSWORD, new String[]{UserRole.MNG.name()});
        mongoTemplate.save(user);

        repository
                .findById(ACCOUNT_ID_NO_USERS)
                .ifPresent((account) -> {
                    account.getUsers().add(user);
                    account.getUsers().add(user);
                    repository.save(account);
                });

        Optional<Account> account = repository.findById(ACCOUNT_ID_NO_USERS);

        Assertions.assertTrue(account.isPresent(), "Account must be present");
        Assertions.assertAll("Saved user account validation",
                () -> Assertions.assertNotNull(account.get().getUsers(), "User list can not be null"),
                () -> Assertions.assertFalse(account.get().getUsers().isEmpty(), "User list can not be empty"),
                () -> Assertions.assertEquals(
                        1,
                        account.get().getUsers().size(),
                        String.format("User list size must be [%d]", 1)));
    }

    @Test
    @DisplayName("save: remove user - Success")
    void saveWhenRemoveUserReturnsSuccess() {
        Optional<Account> account = repository.findById(ACCOUNT_ID);
        long userDocSize = mongoTemplate.count(new Query(), DBConstants.USER_DOCUMENT);
        int accountUsers = 1;

        account.ifPresentOrElse( (acc) -> {
            Assertions.assertEquals(
                    accountUsers,
                    acc.getUsers().size(),
                    String.format("User list size must be [%d]", accountUsers));

            User user = acc.getUsers().iterator().next();
            acc.getUsers().remove(user);
            repository.save(acc);
        }, () -> Assertions.fail(String.format("Account with id: [%s] can not be mull", ACCOUNT_ID)));

        Optional<Account> accountSaved = repository.findById(ACCOUNT_ID);

        Assertions.assertEquals(
                userDocSize,
                mongoTemplate.count(new Query(), DBConstants.USER_DOCUMENT),
                String.format("User repository size has to be equal to [%d]", userDocSize));

        Assertions.assertTrue(accountSaved.isPresent(), "Account must be present");
        Assertions.assertAll("Saved user account validation",
                () -> Assertions.assertNotNull(accountSaved.get().getUsers(), "User list can not be null"),
                () -> Assertions.assertEquals(
                        accountUsers - 1,
                        accountSaved.get().getUsers().size(),
                        String.format("User list size must be [%d]", accountUsers - 1)));
    }

    @Test
    @DisplayName("findByUserId: entities found - Success")
    void findByUserIdWhenEntitiesFoundReturnsSuccess() {
        List<Account> accounts = repository.findByUserId(USER_ID);

        Assertions.assertAll(String.format("Accounts by userId [%s] must have [%d] entities", USER_ID, 2),
                () -> Assertions.assertFalse(accounts.isEmpty(), "Account list can not be empty"),
                () -> Assertions.assertEquals(2, accounts.size(), String.format("Account list size must be {%d]", 2)));
    }
}
