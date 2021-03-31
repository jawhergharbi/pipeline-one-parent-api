package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.lead.LeadMapper;
import com.sawoo.pipeline.api.service.lead.LeadService;
import com.sawoo.pipeline.api.service.lead.LeadServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountLeadServiceTest extends BaseLightServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @MockBean(value = LeadServiceImpl.class)
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
                () -> Assertions.assertNotNull(returnedList.get(0).getPerson(), "Person must be informed"),
                () -> Assertions.assertNotNull(returnedList.get(0).getAccount(), "Account must be informed"));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllLeadsWhenAccountEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findAllLeads(ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllLeadsWhenAccountEntityFoundAndLeadListEmptyReturnsSuccess() {
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
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Lead list must be empty"));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllLeadsWhenAccountEntitiesFoundAndLeadListFoundReturnsSuccess() {
        // Set up mocked entities
        int ACCOUNT_LIST_SIZE = 3;
        AtomicInteger leadCount = new AtomicInteger();
        List<Account> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    Account account = getMockFactory().newEntity(COMPONENT_ID);
                    account.setLeads(
                            IntStream.range(0, getMockFactory().getFAKER().number().numberBetween(1, 3))
                                    .mapToObj( (lead) -> {
                                        String LEAD_ID = getMockFactory().getComponentId();
                                        leadCount.getAndIncrement();
                                        return getMockFactory().getLeadMockFactory().newEntity(LEAD_ID);
                                    }).collect(Collectors.toList()) );
                    return account;
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(Account::getId).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(ACCOUNT_LIST).when(repository).findAllById(ACCOUNT_IDS);
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        List<LeadDTO> returnedList = getService().findAllLeads(ACCOUNT_IDS.toArray(String[]::new), null);

        // Assertions
        Assertions.assertAll(String.format("Account ids [%s] list must have leads", ACCOUNT_IDS),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Lead list can not be empty"),
                () -> Assertions.assertEquals(
                        leadCount.get(),
                        returnedList.size(),
                        String.format("Lead list size must be [%d]", leadCount.get())));

        verify(repository, times(1)).findAllById(ACCOUNT_IDS);
    }

    @Test
    void findAllLeadsWhenAccountEntitiesFoundAndLeadListFilterByStatusFoundReturnsSuccess() {
        // Set up mocked entities
        int ACCOUNT_LIST_SIZE = 3;
        AtomicInteger deadLeadCount = new AtomicInteger();
        List<Account> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    Account account = getMockFactory().newEntity(COMPONENT_ID);
                    account.setLeads(
                            IntStream.range(0, getMockFactory().getFAKER().number().numberBetween(1, 3))
                                    .mapToObj( (l) -> {
                                        String LEAD_ID = getMockFactory().getComponentId();
                                        Lead lead = getMockFactory().getLeadMockFactory().newEntity(LEAD_ID);
                                        if (lead.getStatus().getValue() == LeadStatusList.INDIVIDUALLY_APPROACHED.getStatus()) {
                                            deadLeadCount.getAndIncrement();
                                        }
                                        return lead;
                                    }).collect(Collectors.toList()) );
                    return account;
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(Account::getId).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(ACCOUNT_LIST).when(repository).findAllById(ACCOUNT_IDS);
        doReturn(new LeadMapper()).when(leadService).getMapper();

        // Execute the service call
        List<LeadDTO> returnedList = getService().findAllLeads(
                ACCOUNT_IDS.toArray(String[]::new),
                new Integer[]{LeadStatusList.INDIVIDUALLY_APPROACHED.getStatus()});

        // Assertions
        Assertions.assertAll(String.format("Account ids [%s] list must have leads", ACCOUNT_IDS),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Lead list can not be empty"),
                () -> Assertions.assertEquals(
                        deadLeadCount.get(),
                        returnedList.size(),
                        String.format("Lead list size must be [%d]", deadLeadCount.get())));

        verify(repository, times(1)).findAllById(ACCOUNT_IDS);
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
        AccountService service = getService();
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeLead(LEAD_ID, ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, times(1)).findById(anyString());
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
        AccountService service = getService();
        Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.removeLead(LEAD_ID, ACCOUNT_ID),
                String.format("Must throw CommonServiceException for account id [%s] and lead id [%s]", ACCOUNT_ID, LEAD_ID));

        verify(repository, times(1)).findById(anyString());
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
    void createLeadWhenAccountEntityFoundLeadNotValidPersonNullReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        mockedLeadToCreate.setPerson(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw ConstraintViolationException");
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadNotValidPersonNotValidReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        mockedLeadToCreate.getPerson().setFirstName(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw ConstraintViolationException");
    }

    @Test
    void createLeadWhenAccountEntityFoundLeadValidPersonIdInformedReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        LeadDTO mockedLead = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID, mockedLeadToCreate);
        mockedLeadToCreate.setPerson(PersonDTO.builder().id(LEAD_ID).build());

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
        String LINKED_IN_URL = getMockFactory().getFAKER().internet().url();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        Lead lead = getMockFactory().getLeadMockFactory().newEntity(LEAD_ID);
        lead.getPerson().setLinkedInUrl(LINKED_IN_URL);
        spyAccount.getLeads().add(lead);
        LeadDTO mockedLeadToCreate = getMockFactory().getLeadMockFactory().newDTO(null);
        mockedLeadToCreate.getPerson().setLinkedInUrl(LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.createLead(ACCOUNT_ID, mockedLeadToCreate),
                "Must throw CommonServiceException");

        Assertions.assertEquals(
                ExceptionMessageConstants.ACCOUNT_LEAD_CREATE_LEAD_ALREADY_ADDED_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
        verify(leadService, never()).create(any(LeadDTO.class));
    }

    private void assertCreatedLead(Account spyAccount, LeadDTO returnedLead, String accountId, String leadId) {
        Assertions.assertAll(String.format("Lead add to account id [%s]", accountId),
                () -> Assertions.assertNotNull(returnedLead, "Lead can not be null"),
                () -> Assertions.assertEquals(leadId, returnedLead.getId(), String.format("Lead id must be [%s]", leadId)),
                () -> Assertions.assertNotNull(returnedLead.getCompanyNotes(), "Person can not be null"));
        Assertions.assertFalse(spyAccount.getLeads().isEmpty(), String.format("Lead list of the account id [%s] can not be empty", accountId));

        Assertions.assertFalse(spyAccount.getLeads().isEmpty(), String.format("Lead list of the account id [%s] can not be empty", accountId));

        verify(spyAccount, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Account.class));
    }
}
