package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class AccountRepositoryTest extends BaseRepositoryTest<Account, AccountRepository, AccountMockFactory> {

    private static final String TEST_JSON_DATA_FILE_NAME = "account-test-data.json";
    private static final String ENTITY_ID = "6030d640f3022dc07d72d786";

    private final CompanyRepository companyRepository;

   @Autowired
   public AccountRepositoryTest(AccountRepository repository,
                                AccountMockFactory mockFactory,
                                CompanyRepository companyRepository) {
        super(repository, TEST_JSON_DATA_FILE_NAME, ENTITY_ID, Account.class.getSimpleName(), mockFactory);
        this.companyRepository = companyRepository;
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

    @AfterEach
    protected void afterEach() {
        // Drop the entity collection so we can start fresh
        super.afterEach();
        companyRepository.deleteAll();
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
    @DisplayName("insert: company cascade saving - Success")
    void insertWhenCompanyAccountDoesNotExistReturnsSuccess() {
       Account entity = getMockFactory().newEntity(null);
       entity.getCompany().setId(null);

       Account savedEntity =  getRepository().insert(entity);

       Assertions.assertAll("Company entity must be properly stored",
               () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
               () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"));
    }

    @Test
    @DisplayName("searchByFullName: entity found - Success")
    void searchByFullNameWhenEntitiesFoundReturnsSuccess() {
        String SEARCH_TEXT = "Fernandez";
        String SEARCH_TEXT_CASE_INSENSITIVE = "fernandez";

        List<Account> entities =  getRepository().searchByFullName(SEARCH_TEXT);
        List<Account> entitiesIgnoreCase =  getRepository().searchByFullName(SEARCH_TEXT_CASE_INSENSITIVE);

        Assertions.assertAll("Account search text by the full name: last name capitalized",
                () -> Assertions.assertFalse(entities.isEmpty(), "List of accounts found can not be empty"),
                () -> Assertions.assertEquals(
                        2,
                        entities.size(),
                        String.format(
                                "List of account found with the text [%s] in the [fullName] has to be [%d]",
                                SEARCH_TEXT,
                                2)));

        Assertions.assertAll("Account search text by the full name: last name lower case",
                () -> Assertions.assertFalse(entitiesIgnoreCase.isEmpty(), "List of accounts found can not be empty"),
                () -> Assertions.assertEquals(
                        2,
                        entitiesIgnoreCase.size(),
                        String.format(
                                "List of account found with the text [%s] in the [fullName] has to be [%d]",
                                SEARCH_TEXT_CASE_INSENSITIVE,
                                2)));
    }

    @Test
    @DisplayName("findAllById: entities found - Success")
    void findAllByIdWhenEntitiesFoundReturnsSuccess() {
        int listSize = 3;
        List<Account> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<String> ids = entityList.stream().limit(listSize - 1).map(Account::getId).collect(Collectors.toList());

        getRepository().insert(entityList);

        Iterable<Account> accountsFound = getRepository().findAllById(ids);

        Assertions.assertAll("Assert accounts found by a list of ids",
                () -> Assertions.assertTrue(
                        accountsFound.iterator().hasNext(),
                        "List of accounts can not be empty"),
                () -> Assertions.assertEquals(
                        ids.size(),
                        accountsFound.spliterator().getExactSizeIfKnown(),
                        String.format("List size must be [%d]", ids.size())));
    }

    @Test
    @DisplayName("insert: company cascade updating - Success")
    void insertWhenCompanyDoesExistAndCompanyIsUpdatedReturnsSuccess() {
        // Arrange
        Company company = getMockFactory().getCompanyMockFactory().newEntity(null);
        company = companyRepository.insert(company);
        String COMPANY_ID = company.getId();
        Account entity = getMockFactory().newEntity(null);
        String COMPANY_URL_UPDATED = getMockFactory().getFAKER().company().url();
        company.setUrl(COMPANY_URL_UPDATED);
        entity.setCompany(company);

        // execute repository action
        Account savedEntity =  getRepository().insert(entity);
        Optional<Company> updatedCompany = companyRepository.findById(COMPANY_ID);


        // assertions
        Assertions.assertAll("Company entity must be properly updated",
                () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
                () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"),
                () -> Assertions.assertTrue(updatedCompany.isPresent(), "Company updated entity can not be null"),
                () -> updatedCompany.ifPresent( c -> Assertions.assertEquals(
                        COMPANY_URL_UPDATED,
                        c.getUrl(),
                        String.format("Company url must be [%s]", COMPANY_URL_UPDATED))));
    }

    @Test
    @DisplayName("findAllById: entities found but not for all of the ids in the list - Success")
    void findAllByIdWhenEntitiesFoundButNotAllIdsAllReturnsSuccess() {
        int listSize = 3;
        List<Account> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<String> ids = entityList.stream().limit(listSize - 1).map(Account::getId).collect(Collectors.toList());
        // add extra Id not present in the entity list
        ids.add(getMockFactory().getFAKER().internet().uuid());

        getRepository().insert(entityList);

        Iterable<Account> accountsFound = getRepository().findAllById(ids);

        Assertions.assertAll("Assert accounts found by a list of ids",
                () -> Assertions.assertTrue(
                        accountsFound.iterator().hasNext(),
                        "List of accounts can not be empty"),
                () -> Assertions.assertEquals(
                        ids.size() - 1,
                        accountsFound.spliterator().getExactSizeIfKnown(),
                        String.format("List size must be [%d]", (ids.size() - 1))));
    }

    @Test
    @DisplayName("findAllById: entities not found - Success")
    void findAllByIdWhenEntitiesNotFoundReturnsSuccess() {
        int listSize = 2;
        List<String> ids = IntStream
                .range(0, listSize)
                .mapToObj(s -> getMockFactory().getFAKER().internet().uuid())
                .collect(Collectors.toList());

        Iterable<Account> accountsFound = getRepository().findAllById(ids);

        Assertions.assertAll("Assert accounts found by a list of ids",
                () -> Assertions.assertFalse(
                        accountsFound.iterator().hasNext(),
                        "List of accounts can not have any element"),
                () -> Assertions.assertEquals(
                        0,
                        accountsFound.spliterator().getExactSizeIfKnown(),
                        String.format("List size must be [%d]", 0)));
    }
}
