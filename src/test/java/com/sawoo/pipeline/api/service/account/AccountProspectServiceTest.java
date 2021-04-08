package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.prospect.ProspectMapper;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import com.sawoo.pipeline.api.service.prospect.ProspectServiceImpl;
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
class AccountProspectServiceTest extends BaseLightServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @MockBean(value = ProspectServiceImpl.class)
    private ProspectService prospectService;

    @Autowired
    public AccountProspectServiceTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory, DBConstants.ACCOUNT_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    void findAllProspectsWhenAccountEntityFoundAndProspectsFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int PROSPECT_LIST_SIZE = 3;
        List<Prospect> prospectList = IntStream.range(0, PROSPECT_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getProspectMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        mockedAccount.setProspects(prospectList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        List<ProspectDTO> returnedList = getService().findAllProspects(ACCOUNT_ID);

        // Assertions
        Assertions.assertAll(String.format("Account id [%s] must have [%d] prospect/s", ACCOUNT_ID, PROSPECT_LIST_SIZE),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Prospect list can not be empty"),
                () -> Assertions.assertEquals(
                        PROSPECT_LIST_SIZE,
                        prospectList.size(),
                        String.format("Prospect list size must be equal to [%d]", PROSPECT_LIST_SIZE)),
                () -> Assertions.assertNotNull(returnedList.get(0).getPerson(), "Person must be informed"),
                () -> Assertions.assertNotNull(returnedList.get(0).getAccount(), "Account must be informed"));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllProspectsWhenAccountEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findAllProspects(ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllProspectsWhenAccountEntityFoundAndProspectListEmptyReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        List<ProspectDTO> returnedList = getService().findAllProspects(ACCOUNT_ID);

        // Assertions
        Assertions.assertAll(String.format("Account id [%s] must have no prospects", ACCOUNT_ID),
                () -> Assertions.assertTrue(returnedList.isEmpty(), "Prospect list must be empty"));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void findAllProspectsWhenAccountEntitiesFoundAndProspectListFoundReturnsSuccess() {
        // Set up mocked entities
        int ACCOUNT_LIST_SIZE = 3;
        AtomicInteger prospectCount = new AtomicInteger();
        List<Account> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    Account account = getMockFactory().newEntity(COMPONENT_ID);
                    account.setProspects(
                            IntStream.range(0, getMockFactory().getFAKER().number().numberBetween(1, 3))
                                    .mapToObj( (p) -> {
                                        String PROSPECT_ID = getMockFactory().getComponentId();
                                        prospectCount.getAndIncrement();
                                        return getMockFactory().getProspectMockFactory().newEntity(PROSPECT_ID);
                                    }).collect(Collectors.toList()) );
                    return account;
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(Account::getId).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(ACCOUNT_LIST).when(repository).findAllById(ACCOUNT_IDS);
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        List<ProspectDTO> returnedList = getService().findAllProspects(ACCOUNT_IDS.toArray(String[]::new), null);

        // Assertions
        Assertions.assertAll(String.format("Account ids [%s] list must have prospects", ACCOUNT_IDS),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Prospect list can not be empty"),
                () -> Assertions.assertEquals(
                        prospectCount.get(),
                        returnedList.size(),
                        String.format("Prospect list size must be [%d]", prospectCount.get())));

        verify(repository, times(1)).findAllById(ACCOUNT_IDS);
    }

    @Test
    void findAllProspectsWhenAccountEntitiesFoundAndProspectListFilterByStatusFoundReturnsSuccess() {
        // Set up mocked entities
        int ACCOUNT_LIST_SIZE = 3;
        AtomicInteger deadProspectCount = new AtomicInteger();
        List<Account> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    Account account = getMockFactory().newEntity(COMPONENT_ID);
                    account.setProspects(
                            IntStream.range(0, getMockFactory().getFAKER().number().numberBetween(1, 3))
                                    .mapToObj( (l) -> {
                                        String PROSPECT_ID = getMockFactory().getComponentId();
                                        Prospect prospect = getMockFactory().getProspectMockFactory().newEntity(PROSPECT_ID);
                                        if (prospect.getQualification().getValue() == ProspectQualification.LEAD.getValue()) {
                                            deadProspectCount.getAndIncrement();
                                        }
                                        return prospect;
                                    }).collect(Collectors.toList()) );
                    return account;
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(Account::getId).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(ACCOUNT_LIST).when(repository).findAllById(ACCOUNT_IDS);
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        List<ProspectDTO> returnedList = getService().findAllProspects(
                ACCOUNT_IDS.toArray(String[]::new),
                new Integer[]{ProspectQualification.LEAD.getValue()});

        // Assertions
        Assertions.assertAll(String.format("Account ids [%s] list must have prospects", ACCOUNT_IDS),
                () -> Assertions.assertFalse(returnedList.isEmpty(), "Prospect list can not be empty"),
                () -> Assertions.assertEquals(
                        deadProspectCount.get(),
                        returnedList.size(),
                        String.format("Prospect list size must be [%d]", deadProspectCount.get())));

        verify(repository, times(1)).findAllById(ACCOUNT_IDS);
    }

    @Test
    void removeProspectWhenAccountEntityFoundAndProspectFoundReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int PROSPECT_LIST_SIZE = 3;
        List<Prospect> prospectList = IntStream.range(0, PROSPECT_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getProspectMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<Prospect> spyProspectList = spy(prospectList);
        mockedAccount.setProspects(spyProspectList);


        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        String PROSPECT_ID = prospectList.get(0).getId();
        ProspectDTO returnedDTO = getService().removeProspect(ACCOUNT_ID, PROSPECT_ID);

        Assertions.assertAll(String.format("Prospect id [%s] removed from account id [%s]", PROSPECT_ID, ACCOUNT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Deleted prospect can not be null"),
                () -> Assertions.assertEquals(PROSPECT_ID, returnedDTO.getId(), String.format("Prospect id has to be [%s]", PROSPECT_ID)),
                () -> Assertions.assertEquals(PROSPECT_LIST_SIZE - 1, mockedAccount.getProspects().size(), "dasd"));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Account.class));
        verify(prospectService, times(1)).delete(anyString());
        verify(spyProspectList, times(1)).remove(any(Prospect.class));
    }

    @Test
    void removeProspectWhenAccountEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeProspect(PROSPECT_ID, ACCOUNT_ID),
                String.format("Must throw ResourceNotFoundException for account id [%s]", ACCOUNT_ID));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void removeProspectWhenAccountEntityFoundAndProspectNotFoundReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);

        int PROSPECT_LIST_SIZE = 3;
        List<Prospect> prospectList = IntStream.range(0, PROSPECT_LIST_SIZE)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getProspectMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        List<Prospect> spyProspectList = spy(prospectList);
        mockedAccount.setProspects(spyProspectList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.removeProspect(PROSPECT_ID, ACCOUNT_ID),
                String.format("Must throw CommonServiceException for account id [%s] and prospect id [%s]", ACCOUNT_ID, PROSPECT_ID));

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    void createProspectWhenAccountEntityFoundProspectValidReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        ProspectDTO mockedProspectToCreate = getMockFactory().getProspectMockFactory().newDTO(null);
        ProspectDTO mockedProspect = getMockFactory().getProspectMockFactory().newDTO(PROSPECT_ID, mockedProspectToCreate);


        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());
        doReturn(mockedProspect).when(prospectService).create(any(ProspectDTO.class));
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        ProspectDTO returnedDTO = getService().createProspect(ACCOUNT_ID, mockedProspectToCreate);

        // Assertions
        assertCreatedProspect(spyAccount, returnedDTO, ACCOUNT_ID, PROSPECT_ID);
    }

    @Test
    void createProspectWhenAccountEntityFoundProspectNotValidPersonNullReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        ProspectDTO mockedProspectToCreate = getMockFactory().getProspectMockFactory().newDTO(null);
        mockedProspectToCreate.setPerson(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.createProspect(ACCOUNT_ID, mockedProspectToCreate),
                "Must throw ConstraintViolationException");
    }

    @Test
    void createProspectWhenAccountEntityFoundProspectNotValidPersonNotValidReturnsFailure() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account mockedAccount = getMockFactory().newEntity(ACCOUNT_ID);
        ProspectDTO mockedProspectToCreate = getMockFactory().getProspectMockFactory().newDTO(null);
        mockedProspectToCreate.getPerson().setFirstName(null);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.createProspect(ACCOUNT_ID, mockedProspectToCreate),
                "createProspect must throw ConstraintViolationException");
    }

    @Test
    void createProspectWhenAccountEntityFoundProspectValidPersonIdInformedReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        ProspectDTO mockedProspectToCreate = getMockFactory().getProspectMockFactory().newDTO(null);
        ProspectDTO mockedProspect = getMockFactory().getProspectMockFactory().newDTO(PROSPECT_ID, mockedProspectToCreate);
        mockedProspectToCreate.setPerson(PersonDTO.builder().id(PROSPECT_ID).build());

        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());
        doReturn(mockedProspect).when(prospectService).create(any(ProspectDTO.class));
        doReturn(new ProspectMapper()).when(prospectService).getMapper();

        // Execute the service call
        ProspectDTO returnedDTO = getService().createProspect(ACCOUNT_ID, mockedProspectToCreate);

        // Assertions
        assertCreatedProspect(spyAccount, returnedDTO, ACCOUNT_ID, PROSPECT_ID);
    }

    @Test
    void createProspectWhenAccountEntityFoundProspectAlreadyAddedProspectReturnsFailure() {
        // Set up mocked entities
        String LINKED_IN_URL = getMockFactory().getFAKER().internet().url();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getMockFactory().getComponentId();
        Account spyAccount = spy(getMockFactory().newEntity(ACCOUNT_ID));
        Prospect prospect = getMockFactory().getProspectMockFactory().newEntity(PROSPECT_ID);
        prospect.getPerson().setLinkedInUrl(LINKED_IN_URL);
        spyAccount.getProspects().add(prospect);
        ProspectDTO mockedProspectToCreate = getMockFactory().getProspectMockFactory().newDTO(null);
        mockedProspectToCreate.getPerson().setLinkedInUrl(LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.of(spyAccount)).when(repository).findById(anyString());

        // Assertions
        AccountService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.createProspect(ACCOUNT_ID, mockedProspectToCreate),
                " createProspect must throw CommonServiceException");

        Assertions.assertEquals(
                ExceptionMessageConstants.ACCOUNT_PROSPECT_CREATE_PROSPECT_ALREADY_ADDED_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
        verify(prospectService, never()).create(any(ProspectDTO.class));
    }

    private void assertCreatedProspect(Account spyAccount, ProspectDTO returnedProspect, String accountId, String prospectId) {
        Assertions.assertAll(String.format("Prospect add to account id [%s]", accountId),
                () -> Assertions.assertNotNull(returnedProspect, "Prospect can not be null"),
                () -> Assertions.assertEquals(prospectId, returnedProspect.getId(), String.format("Prospect id must be [%s]", prospectId)),
                () -> Assertions.assertNotNull(returnedProspect.getCompanyNotes(), "Person can not be null"));
        Assertions.assertFalse(spyAccount.getProspects().isEmpty(), String.format("Prospect list of the account id [%s] can not be empty", accountId));

        Assertions.assertFalse(spyAccount.getProspects().isEmpty(), String.format("Prospect list of the account id [%s] can not be empty", accountId));

        verify(spyAccount, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Account.class));
    }
}
