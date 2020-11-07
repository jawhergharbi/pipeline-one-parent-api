package com.sawoo.pipeline.api.service;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import com.sawoo.pipeline.api.repository.client.datastore.ClientRepository;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientServiceTest extends BaseServiceTest {

    @Autowired
    private DataStoreKeyFactory dataStoreKeyFactory;

    @Autowired
    private ClientService service;

    @MockBean
    private ClientRepository repository;

    @SpyBean
    private CompanyService companyService;

    @SpyBean
    private UserAuthJwtService userService;

    @Test
    @DisplayName("Client Service: create when client does not exist - Success")
    void createWhenClientDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();

        ClientBasicDTO mockedDTO = getMockFactory().newClientDTO(null, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());
        ClientBasicDTO spyDTO = spy(mockedDTO);

        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(FAKER.number().randomNumber(), COMPANY_NAME, COMPANY_URL));

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(CLIENT_LINKED_IN_URL);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.empty()).when(companyService).findByName(COMPANY_NAME);

        // Execute the service call
        ClientBasicDTO returnedEntity = service.create(spyDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, String.format("Client entity with LinkedInUrl [%s] was found already in the system", CLIENT_LINKED_IN_URL));
        Assertions.assertEquals(CLIENT_FULL_NAME, returnedEntity.getFullName(), "Client.fullName should be the same");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByLinkedInUrl(any());

        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(any());
        verify(companyService).findByName(companyNameCaptor.capture());
        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));

        verify(spyDTO, times(1)).setStatus(any());
        verify(spyDTO, never()).setCompany(any());
    }

    @Test
    @DisplayName("Client Service: create when client does not exist but company exists - Success")
    void createWhenClientDoesNotExistAndCompanyDoesExistReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();

        ClientBasicDTO mockedDTO = getMockFactory().newClientDTO(null, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());
        ClientBasicDTO spyDTO = spy(mockedDTO);

        String EXISTING_COMPANY_ID = FAKER.internet().uuid();
        LocalDateTime EXISTING_COMPANY_DATETIME = LocalDateTime.of(2020, 12, 31, 12, 0);
        CompanyDTO existingCompanyDTO = getMockFactory().newCompanyDTO(EXISTING_COMPANY_ID, COMPANY_NAME, COMPANY_URL, EXISTING_COMPANY_DATETIME);

        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(FAKER.number().randomNumber(), COMPANY_NAME, COMPANY_URL));

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(CLIENT_LINKED_IN_URL);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.of(existingCompanyDTO)).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        ClientBasicDTO returnedEntity = service.create(spyDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, String.format("Client entity with LinkedInUrl [%s] was found already in the system", CLIENT_LINKED_IN_URL));
        Assertions.assertEquals(CLIENT_FULL_NAME, returnedEntity.getFullName(), "Client.fullName should be the same");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByLinkedInUrl(CLIENT_LINKED_IN_URL);

        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(any());
        verify(companyService).findByName(companyNameCaptor.capture());
        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));

        verify(spyDTO, times(1)).setStatus(any());
        verify(spyDTO, times(1)).setCompany(any());
    }

    @Test
    @DisplayName("Client service: create when client does exist - Failure")
    void createWhenClientExistsReturnsCommonException() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        mockedDTO.setLinkedInUrl(CLIENT_LINKED_IN_URL);

        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByLinkedInUrl(CLIENT_LINKED_IN_URL);

        // Asserts
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(mockedDTO),
                "create must throw a CommonServiceException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION);
        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");

        verify(repository, times(1)).findByLinkedInUrl(any());
    }

    @Test
    @DisplayName("Client Service: delete client entity found - Success")
    void deleteWhenCompanyEntityFoundReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();

        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedDTO = service.delete(CLIENT_ID);

        Assertions.assertTrue(returnedDTO.isPresent(), "Returned entity can not be null");
        Assertions.assertEquals(CLIENT_ID, returnedDTO.get().getId(), "Client.id fields must be the same");
        Assertions.assertEquals(CLIENT_FULL_NAME, returnedDTO.get().getFullName(), "Client.fullName fields must be the the same");
        Assertions.assertNotNull(returnedDTO.get().getCompany(), "Client.company can not be null");

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(repository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Client Service: delete client entity not found - Null entity")
    void deleteWhenClientEntityNotFoundReturnsNullEntity() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedEntity = service.delete(CLIENT_ID);

        Assertions.assertFalse(returnedEntity.isPresent(), "Returned entity must be null");

        verify(repository, times(1)).findById(CLIENT_ID);
    }

    @Test
    @DisplayName("Client Service: findById - Success")
    void findByIdWhenClientExitsReturnsSuccess() {
        // Set up mock entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedEntity = service.findById(CLIENT_ID);

        // Assert the response
        Assertions.assertTrue(returnedEntity.isPresent(), "Client entity with id " + CLIENT_ID + " was not found");
        Assertions.assertEquals(CLIENT_ID, returnedEntity.get().getId(), "Client.id should be the same");
        Assertions.assertEquals(0, returnedEntity.get().getLeadsSize(), "Client must have no leads");

        verify(repository, times(1)).findById(CLIENT_ID);
    }

    @Test
    @DisplayName("Client Service: findById - Success")
    void findByIdWhenClientExitsAndContainsLeadsReturnsSuccess() {
        // Set up mock entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().firstName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
        Lead leadMockedEntity = getMockFactory().newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_THREAD_URL, true);
        mockedEntity.getLeads().add(leadMockedEntity);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedEntity = service.findById(CLIENT_ID);

        // Assert the response
        Assertions.assertTrue(returnedEntity.isPresent(), "Client entity with id " + CLIENT_ID + " was not found");
        Assertions.assertEquals(CLIENT_ID, returnedEntity.get().getId(), "Client.id should be the same");
        Assertions.assertEquals(1, returnedEntity.get().getLeadsSize(), String.format("Client must have %d leads", 1));

        verify(repository, times(1)).findById(CLIENT_ID);
    }

    @Test
    @DisplayName("Client Service: findById when client does not exists - Failure")
    void findByIdWhenClientIsNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedEntity = service.findById(CLIENT_ID);

        // Asserts
        Assertions.assertFalse(returnedEntity.isPresent(), "Client entity with id " + CLIENT_ID + " must be null");

        verify(repository, times(1)).findById(CLIENT_ID);
    }

    @Test
    @DisplayName("Client Service: findAll empty list - Success")
    void findAllWhenThereAreNoClientsEntitiesReturnsSuccess() {
        // Set up mock entities
        List<Client> clients = Collections.emptyList();

        // Set up the mocked repository
        doReturn(clients).when(repository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAll();

        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Client Service: findAll - Success")
    void findAllWhenThereAreThreeLeadsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<Client> leadList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    return getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(leadList).when(repository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAll();

        Assertions.assertEquals(listSize, returnedList.size(), String.format("Returned list size must be %d", listSize));

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Client service: findAllMain - Success")
    void findAllMainWhenThereAreTwoClientsAndNoLeadsReturnsSuccess() {
        // Set up mock entities
        int listSize = 2;
        List<Client> leadList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    return getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
                }).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(leadList).when(repository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAllMain(now);

        Assertions.assertEquals(listSize, returnedList.size(), String.format("Returned list size must be [%d]", listSize));

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Client service: findAllMain when multiple leads and all have future interactions - Success")
    void findAllMainWhenThereIsOneClientWithMultipleLeadsAndNextFoundReturnsSuccess() {
        // Set up mock entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client clientEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        int leadListSize = 3;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<Lead> leadList = IntStream.range(0, leadListSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    Lead leadEntity = getMockFactory().newLeadEntity(LEAD_ID, true);
                    Long LEAD_INTERACTION_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    String LEAD_INTERACTION_INVITE = FAKER.internet().url();
                    LeadInteraction leadInteraction = newLeadInteractionEntity(LEAD_ID, LEAD_INTERACTION_ID, 0, 0, LEAD_INTERACTION_INVITE);
                    leadInteraction.setScheduled(now
                            .plusDays(FAKER.number().numberBetween(1, 30))
                            .minusHours(FAKER.number().numberBetween(1, 10)));
                    leadEntity.getInteractions().add(leadInteraction);
                    return leadEntity;
                }).collect(Collectors.toList());
        clientEntity.getLeads().addAll(leadList);

        // Set up the mocked repository
        doReturn(Collections.singletonList(clientEntity)).when(repository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAllMain(now);

        Assertions.assertEquals(1, returnedList.size(), String.format("Returned list size must be [%d]", 1));
        Assertions.assertEquals(leadListSize, returnedList.get(0).getLeadsSize(), String.format("Returned list size must be [%d]", leadListSize));

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Client Service: update client name and linkedIn url when client does exist - Success")
    void updateWhenClientFoundReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        mockedDTO.setFullName(CLIENT_FULL_NAME);
        mockedDTO.setLinkedInUrl(CLIENT_LINKED_IN_URL);

        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(CLIENT_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedDTO = service.update(CLIENT_ID, mockedDTO);

        Assertions.assertTrue(returnedDTO.isPresent(), "Client entity is not null");
        Assertions.assertEquals(
                CLIENT_FULL_NAME,
                returnedDTO.get().getFullName(),
                String.format("Full Name must be '%s'", CLIENT_FULL_NAME));
        Assertions.assertEquals(
                CLIENT_LINKED_IN_URL,
                returnedDTO.get().getLinkedInUrl(),
                String.format("LinkedIn Url must be '%s'", CLIENT_LINKED_IN_URL));

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Client Service: update CSM when client does exist - Success")
    void updateCSMWhenClientFoundAndUserFoundReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client mockedClientEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.CSM.name()});

        // Set up the mocked repository
        doReturn(Optional.of(mockedClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTO).when(userService).findById(USER_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedDTO = service.updateCSM(CLIENT_ID, USER_ID);

        Assertions.assertTrue(returnedDTO.isPresent(), "Client can not be null");
        Assertions.assertNotNull(returnedDTO.get().getCustomerSuccessManager(), "Customer Success Manager can not be null");
        Assertions.assertEquals(
                USER_ID,
                returnedDTO.get().getCustomerSuccessManager().getId(),
                String.format("Customer Success Manager id must be %s", USER_ID));

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(userService, times(1)).findById(USER_ID);
    }

    @Test
    @DisplayName("Client Service: update CSM when client does exist and SA matches CSM - Failure")
    void updateCSMWhenClientFoundCSMDoesMatchSAReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name(), Role.CSM.name()});
        spyClientEntity.setSalesAssistant(mockedUserEntity);

        UserDTO mockedUserDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name(), Role.CSM.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTO).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateCSM(CLIENT_ID, USER_ID),
                "updateCSM must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_CSM_MATCH_SA_EXCEPTION);
        Assertions.assertEquals(3, exception.getArgs().length);

        verify(spyClientEntity, never()).setCustomerSuccessManager(any());
    }

    @Test
    @DisplayName("Client Service: update SA when client does exist and CSM matches SA - Failure")
    void updateSAWhenClientFoundCSMDoesMatchSAReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.CSM.name(), Role.SA.name()});
        spyClientEntity.setCustomerSuccessManager(mockedUserEntity);

        UserDTO mockedUserDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.CSM.name(), Role.SA.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTO).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateSA(CLIENT_ID, USER_ID),
                "updateSA must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_SA_MATCH_CSM_EXCEPTION);
        Assertions.assertEquals(3, exception.getArgs().length);

        verify(spyClientEntity, never()).setSalesAssistant(any());
    }

    @Test
    @DisplayName("Client Service: update CSM when client does exist - Failure")
    void updateCSMWhenClientFoundAndUserRolesIsNotCorrectReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.CSM.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTO).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateSA(CLIENT_ID, USER_ID),
                "updateSA must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_SA_MUST_HAVE_ROLE_SA_EXCEPTION);
        Assertions.assertEquals(4, exception.getArgs().length);

        verify(spyClientEntity, never()).setCustomerSuccessManager(any());

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(userService, times(1)).findById(USER_ID);
    }

    private LeadInteraction newLeadInteractionEntity(Long leadId, Long id, int status, int type, String urlInvite) {
        LeadInteraction mockedEntity = new LeadInteraction();
        mockedEntity.setKey(createKey(leadId, id));
        mockedEntity.setStatus(status);
        mockedEntity.setType(type);
        mockedEntity.setInvite(UrlTitle.builder().url(urlInvite).build());
        return mockedEntity;
    }

    private Key createKey(Long leadId, Long interactionId) {
        Key parentKey = dataStoreKeyFactory.getKeyFactory(DataStoreConstants.LEAD_ENTITY_ENTITY).newKey(leadId);
        return Key.newBuilder(parentKey, DataStoreConstants.LEAD_INTERACTION_ENTITY_NAME, interactionId).build();
    }
}
