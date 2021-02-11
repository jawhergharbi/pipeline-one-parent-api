package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

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
