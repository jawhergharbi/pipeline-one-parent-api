package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class AccountRepositoryTest extends BaseRepositoryTest<Account, AccountRepository, AccountMockFactory> {

    private static final File ACCOUNT_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "account-test-data.json").toFile();
    private static final String ACCOUNT_ID = "5fce6a364d9da6645ba36";

   @Autowired
   public AccountRepositoryTest(AccountRepository repository, AccountMockFactory mockFactory) {
        super(repository, ACCOUNT_JSON_DATA, ACCOUNT_ID, Account.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Account[]> getClazz() {
        return Account[].class;
    }

    @Override
    protected String getComponentId(Account component) {
        return component.getId();
    }

    @Override
    protected Account getNewEntity() {
        String ACCOUNT_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(ACCOUNT_ID);
    }

    @Test
    @DisplayName("findByEmail: entity found - Success")
    void findByEmailWhenEntityFoundReturnsSuccess() {
        String ACCOUNT_EMAIL = "manuel.fernandez@gmail.com";
        Optional<Account> entity = getRepository().findByEmail(ACCOUNT_EMAIL);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("Account with email [%s] can not be null", ACCOUNT_EMAIL));
        Assertions.assertEquals(
                ACCOUNT_EMAIL,
                entity.get().getEmail(),
                String.format("Account.email must be [%s]", ACCOUNT_EMAIL));
    }

    @Test
    @DisplayName("findByLinkedInUrl: entity found - Success")
    void findByLinkedInUrlWhenEntityFoundReturnsSuccess() {
        String ACCOUNT_LINKED_IN_URL = "http://linkedin.com/manuel.fernandez";
        Optional<Account> entity = getRepository().findByLinkedInUrl(ACCOUNT_LINKED_IN_URL);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("Account with linkedInUrl [%s] can not be null", ACCOUNT_LINKED_IN_URL));
        Assertions.assertEquals(
                ACCOUNT_LINKED_IN_URL,
                entity.get().getLinkedInUrl(),
                String.format("Account.linkedInUrl must be [%s]", ACCOUNT_LINKED_IN_URL));
    }

    @Test
    @DisplayName("save: company cascade saving - Success")
    void saveWhenCompanyDoesNotExistReturnsSuccess() {
       Account entity = getMockFactory().newEntity(getComponentId());
       entity.getCompany().setId(null);

       Account savedEntity =  getRepository().save(entity);

       Assertions.assertAll("Company entity must be properly stored",
               () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
               () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"));
    }
}
