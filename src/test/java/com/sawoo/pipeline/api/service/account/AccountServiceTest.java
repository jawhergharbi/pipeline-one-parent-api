package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTest extends BaseServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

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

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("deleteAccountNotes: entity does exist - Success")
    void deleteAccountNotesWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account entity = getMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(entity)).when(repository).findById(anyString());

        // Execute the service call
        AccountDTO returnedDTO = getService().deleteAccountNotes(ACCOUNT_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Account can not be null");
        Assertions.assertNull(returnedDTO.getNotes(), "Account notes must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Account.class));
    }

    @Test
    @DisplayName("deleteAccountNotes: entity does not exist - Failure")
    void deleteAccountNotesWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().deleteAccountNotes(ACCOUNT_ID),
                "deleteAccountNotes must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("deleteAccountNotes: account id invalid (empty string) - Failure")
    void deleteAccountNotesWhenProspectIdInvalidReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        AccountService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteAccountNotes(ACCOUNT_ID),
                "deleteAccountNotes must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("deleteAccountCompanyNotes: entity does exist - Success")
    void deleteAccountCompanyNotesWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account entity = getMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(entity)).when(repository).findById(anyString());

        // Execute the service call
        AccountDTO returnedDTO = getService().deleteAccountCompanyNotes(ACCOUNT_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Account can not be null");
        Assertions.assertNull(returnedDTO.getCompanyNotes(), "Account company notes must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Account.class));
    }

    @Test
    @DisplayName("deleteAccountCompanyNotes: entity does not exist - Failure")
    void deleteAccountCompanyNotesWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().deleteAccountCompanyNotes(ACCOUNT_ID),
                "deleteAccountCompanyNotes must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("deleteAccountCompanyNotes: account id invalid (empty string) - Failure")
    void deleteAccountCompanyNotesWhenProspectIdInvalidReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        AccountService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteAccountCompanyNotes(ACCOUNT_ID),
                "deleteAccountCompanyNotes must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Account.class));
    }
}
