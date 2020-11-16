package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest extends BaseServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @Autowired
    public AccountServiceTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory, DBConstants.ACCOUNT_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Account component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(AccountDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Account entity) {
        doReturn(Optional.of(entity)).when(repository).findByLinkedInUrl(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String ACCOUNT_LINKED_IN_URL = getMockFactory().getFAKER().internet().url();
        AccountDTO mockedDTO = getMockFactory().newDTO(ACCOUNT_ID);
        mockedDTO.setLinkedInUrl(ACCOUNT_LINKED_IN_URL);
        Account mockedEntity = getMockFactory().newEntity(ACCOUNT_ID);
        mockedEntity.setLinkedInUrl(ACCOUNT_LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(anyString());
        doReturn(mockedEntity).when(repository).insert(any(Account.class));

        // Execute the service call
        AccountDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Entity can not be null");
        Assertions.assertEquals(
                ACCOUNT_LINKED_IN_URL,
                returnedEntity.getLinkedInUrl(),
                "Account.linkedInUrl should be the same");
        Assertions.assertEquals(
                LocalDate.now(ZoneOffset.UTC),
                returnedEntity.getCreated().toLocalDate(),
                "Creation time must be today");
        Assertions.assertEquals(
                LocalDate.now(ZoneOffset.UTC),
                returnedEntity.getUpdated().toLocalDate(),
                "Update time must be today");

        verify(repository, Mockito.times(1)).findByLinkedInUrl(anyString());
        verify(repository, Mockito.times(1)).insert(any(Account.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        AccountDTO mockedDTO = new AccountDTO();
        String ACCOUNT_NEW_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        mockedDTO.setFullName(ACCOUNT_NEW_FULL_NAME);
        mockedDTO.setId(ACCOUNT_ID);
        Account mockedEntity = getMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(ACCOUNT_ID);

        // Execute the service call
        AccountDTO returnedDTO = getService().update(ACCOUNT_ID, mockedDTO);

        Assertions.assertNotNull(returnedDTO, "Account entity can not be null");
        Assertions.assertEquals(
                ACCOUNT_NEW_FULL_NAME,
                returnedDTO.getFullName(),
                String.format("FullName must be '%s'", ACCOUNT_NEW_FULL_NAME));

        verify(repository, Mockito.times(1)).findById(anyString());
        verify(repository, Mockito.times(1)).save(any());
    }
}
