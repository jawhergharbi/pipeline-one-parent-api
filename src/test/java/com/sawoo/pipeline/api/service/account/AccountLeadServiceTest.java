package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.lead.LeadMapper;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountLeadServiceTest extends BaseLightServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @MockBean
    private LeadService leadService;

    @Autowired
    public AccountLeadServiceTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory, DBConstants.ACCOUNT_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    void findAllLeadsWhenAccountEntityFoundAndLeadsFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int LEAD_LIST_SIZE = 3;
        List<Lead> leadList = IntStream.range(0, LEAD_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getLeadMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        mockedAccount.setLeads(leadList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        List<LeadDTO> returnedList = getService().findAllLeads(ACCOUNT_ID);

        // Assertions
        Assertions.assertAll(String.format("Account id [%s] must have [%d] lead/s", ACCOUNT_ID, LEAD_LIST_SIZE),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Lead list can not be empty"),
                () -> Assertions.assertEquals(
                        LEAD_LIST_SIZE,
                        leadList.size(),
                        String.format("Lead list size must be equal to [%d]", LEAD_LIST_SIZE)),
                () -> Assertions.assertNotNull(returnedList.get(0).getProspect(), "Prospect must be informed"));

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    void findAllLeadsWhenAccountEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Assertions
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().findAllLeads(ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    void findAllLeadsWhenAccountEntityFoundAndLeadListEmptyFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        List<LeadDTO> returnedList = getService().findAllLeads(ACCOUNT_ID);

        // Assertions
        Assertions.assertAll(String.format("Account id [%s] must have no leads", ACCOUNT_ID),
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Lead list must be be empty"));

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    void removeLeadWhenAccountEntityFoundAndLeadFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int LEAD_LIST_SIZE = 3;
        List<Lead> leadList = IntStream.range(0, LEAD_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getLeadMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<Lead> spyLeadList = spy(leadList);
        mockedAccount.setLeads(spyLeadList);


        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        String LEAD_ID = leadList.get(0).getId();
        LeadDTO returnedDTO = getService().removeLead(ACCOUNT_ID, LEAD_ID);

        Assertions.assertAll(String.format("Lead id [%s] removed from account id [%s]", LEAD_ID, ACCOUNT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Deleted lead can not be null"),
                () -> Assertions.assertEquals(LEAD_ID, returnedDTO.getId(), String.format("Lead id has to be [%s]", LEAD_ID)),
                () -> Assertions.assertEquals(LEAD_LIST_SIZE - 1, mockedAccount.getLeads().size(), "dasd"));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Account.class));
        verify(leadService, times(1)).delete(anyString());
        verify(spyLeadList, times(1)).remove(any(Lead.class));
    }

    @Test
    void removeLeadWhenAccountEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Assertions
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().removeLead(LEAD_ID, ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    void removeLeadWhenAccountEntityFoundAndLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int LEAD_LIST_SIZE = 3;
        List<Lead> leadList = IntStream.range(0, LEAD_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getLeadMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<Lead> spyLeadList = spy(leadList);
        mockedAccount.setLeads(spyLeadList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().removeLead(LEAD_ID, ACCOUNT_ID),
                String.format("Must throw CommonServiceException for account id [%s] and lead id [%s]", ACCOUNT_ID, LEAD_ID));

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadValidReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        LeadDTO mockedLead = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID, mockedLeadToCreate);


        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());
        doReturn(mockedLead).when(leadService).create(any(LeadDTO.class));
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        LeadDTO returnedDTO = getService().createLead(ACCOUNT_ID, mockedLeadToCreate);

        // Assertions
        assertCreatedLead(spyAccount, returnedDTO, ACCOUNT_ID, LEAD_ID);
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadNotValidProspectNullReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        mockedLeadToCreate.setProspect(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> getService().createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw ConstraintViolationException");
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadNotValidProspectNotValidReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        mockedLeadToCreate.getProspect().setFirstName(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> getService().createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw ConstraintViolationException");
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadValidProspectIdInformedReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        LeadDTO mockedLead = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID, mockedLeadToCreate);
        mockedLeadToCreate.setProspect(ProspectDTO.builder().id(LEAD_ID).build());

        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());
        doReturn(mockedLead).when(leadService).create(any(LeadDTO.class));
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        LeadDTO returnedDTO = getService().createLead(ACCOUNT_ID, mockedLeadToCreate);

        // Assertions
        assertCreatedLead(spyAccount, returnedDTO, ACCOUNT_ID, LEAD_ID);
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadAlreadyAddedLeadReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);

        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());
        doThrow(new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{ DBConstants.LEAD_DOCUMENT, mockedLeadToCreate.toString()})).when(leadService).create(any(LeadDTO.class));

        // Assertions
        // Assertions
        Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw CommonServiceException");
    }

    private void assertCreatedLead(Account spyAccount, LeadDTO returnedLead, String accountId, String leadId) {
        Assertions.assertAll(String.format("Lead add to account id [%s]", accountId),
                () -> Assertions.assertNotNull(returnedLead, "Lead can not be null"),
                () -> Assertions.assertEquals(leadId, returnedLead.getId(), String.format("Lead id must be [%s]", leadId)),
                () -> Assertions.assertNotNull(returnedLead.getCompanyNotes(), "Prospect can not be null"));
        Assertions.assertFalse(spyAccount.getLeads().isEmpty(), String.format("Lead list of the account id [%s] can not be empty", accountId));

        Assertions.assertFalse(spyAccount.getLeads().isEmpty(), String.format("Lead list of the account id [%s] can not be empty", accountId));

        verify(spyAccount, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Account.class));
    }
}
