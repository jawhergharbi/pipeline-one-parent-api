package com.sawoo.pipeline.api.service;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.ClientRepository;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientLeadServiceTest extends BaseServiceTest {

    @Autowired
    private DataStoreKeyFactory dataStoreKeyFactory;

    @Autowired
    private ClientLeadService service;

    @MockBean
    private ClientRepository clientRepository;

    @SpyBean
    private LeadServiceUtils leadServiceUtils;

    @Test
    @DisplayName("ClientLead Service: create when lead does not exist - Success")
    void createWhenClientFoundAndLeadDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        Long LEAD_ID = FAKER.number().randomNumber();
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();

        LeadBasicDTO mockedLeadDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        Client mockedClientEntity = getMockFactory().newClientEntity(CLIENT_ID);
        mockedClientEntity.getLeads()
                .add(getMockFactory()
                        .newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true));

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(CLIENT_ID);
        doNothing().when(leadServiceUtils).preProcessLead(any(), any());
        doReturn(mockedClientEntity).when(clientRepository).save(any());

        // Execute the service call
        LeadBasicDTO returnedEntity = service.create(CLIENT_ID, mockedLeadDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, String.format("Lead entity with LinkedInUrl [%s] was found already in the system", LEAD_LINKED_IN_URL));
        Assertions.assertEquals(LEAD_FULL_NAME, returnedEntity.getFullName(), "Lead.fullName should be the same");
        Assertions.assertEquals(1, mockedClientEntity.getLeads().size(), String.format("Client lead list size must be %d", 1));

        verify(clientRepository, Mockito.times(1)).findById(anyLong());
        verify(leadServiceUtils, Mockito.times(1)).preProcessLead(any(), any());
        verify(clientRepository, Mockito.times(1)).save(any());

        verify(spyClientEntity, Mockito.atLeastOnce()).getLeads();
        verify(spyClientEntity, Mockito.atLeastOnce()).setUpdated(any());
    }

    @Test
    @DisplayName("ClientLead Service: create when client does not exist - Failure")
    void createWhenClientNotFoundReturnsResourceNotFoundException() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();

        LeadBasicDTO mockedLeadDTO = getMockFactory()
                .newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(clientRepository).findById(anyLong());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(CLIENT_ID, mockedLeadDTO),
                "create must throw a ResourceNotFoundException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");

        verify(clientRepository, Mockito.times(1)).findById(anyLong());
        ArgumentCaptor<Long> clientIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(clientRepository).findById(clientIdCaptor.capture());
        Assertions.assertEquals(clientIdCaptor.getValue(), CLIENT_ID, String.format("Client id to be verified must be: [%d]", CLIENT_ID));
    }

    @Test
    @DisplayName("ClientLead Service: create when client found and lead is not correctly saved - Failure")
    void createWhenClientFoundAndLeadNotCorrectlySavedReturnsResourceNotFoundException() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();

        LeadBasicDTO mockedLeadDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        Client mockedClientEntity = getMockFactory().newClientEntity(CLIENT_ID);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));

        // Set up the mocked repository
        doReturn(Optional.of(mockedClientEntity)).when(clientRepository).findById(anyLong());
        doNothing().when(leadServiceUtils).preProcessLead(any(), any());
        doReturn(spyClientEntity).when(clientRepository).save(any());

        // Asserts
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(CLIENT_ID, mockedLeadDTO),
                "create must throw a CommonServiceException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_REFERENCE_CHILD_WAS_NOT_FOUND_ERROR,
                "Exception message must be " + ExceptionMessageConstants.COMMON_REFERENCE_CHILD_WAS_NOT_FOUND_ERROR);
        Assertions.assertEquals(
                4,
                exception.getArgs().length,
                "Number of arguments in the exception must be 4");
    }

    @Test
    @DisplayName("ClientLead Service: findAll when client found and there are three leads - Success")
    void findAllWhenClientFoundAndThereAreLeadsReturnsSuccess() {
        int listSize = 3;
        List<Lead> leadList = getMockedLeadList(listSize);
        Long CLIENT_ID = FAKER.number().randomNumber();
        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID);
        mockedEntity.setLeads(leadList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(clientRepository).findById(anyLong());

        // Execute the service call
        List<LeadBasicDTO> returnedList = service.findAll(CLIENT_ID);

        // Assertions
        Assertions.assertEquals(listSize, returnedList.size(), String.format("Lead list size must be %d", listSize));
    }

    @Test
    @DisplayName("ClientLead Service: findAll when client found and there are no leads - Success")
    void findAllWhenClientFoundAndThereAreNoLeadsReturnsSuccess() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Client mockedEntity = getMockFactory().newClientEntity(CLIENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(clientRepository).findById(anyLong());

        // Execute the service call
        List<LeadBasicDTO> returnedList = service.findAll(CLIENT_ID);

        // Assertions
        Assertions.assertEquals(0, returnedList.size(), String.format("Lead list size must be %d", 0));
    }

    @Test
    @DisplayName("ClientLead Service: findAllMain when client found and there are leads (no next or previous interactions) - Success")
    void findAllMainWhenThereAreMultipleClientsFoundContainingMultipleLeadsReturnsSuccess() {
        int clientListSize = 3;
        List<Client> clientList = IntStream.range(0, clientListSize)
                .mapToObj((client) -> {
                    Long CLIENT_ID = FAKER.number().randomNumber();
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    Client newClient = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
                    List<Lead> leads = getMockedLeadList(FAKER.random().nextInt(3));
                    newClient.setLeads(leads);
                    return newClient;

                }).collect(Collectors.toList());
        int totalLeadListSize = Math.toIntExact( clientList.stream().mapToLong((client) -> client.getLeads().size()).sum() );
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(clientList).when(clientRepository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertEquals(totalLeadListSize, returnedList.size(), String.format("Lead list size must be %d", clientListSize));
    }

    @Test
    @DisplayName("ClientLead Service: findAllMain when client found and there are leads and next or previous interactions for one of the leads - Success")
    void findAllMainWhenThereIsOneClientFoundContainingMultipleLeadsAndNextInteractionReturnsSuccess() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Client clientEntity = getMockFactory().newClientEntity(CLIENT_ID);
        int listSize = 3;
        List<Lead> leadList = getMockedLeadList(listSize);
        clientEntity.getLeads().addAll(leadList);

        Lead lead = leadList.get(0);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Long NEXT_INTERACTION_ID = FAKER.number().randomNumber();
        LeadInteraction nextInteraction = newLeadInteractionEntity(
                createInteractionKey(lead.getId(), NEXT_INTERACTION_ID),
                FAKER.number().numberBetween(0, 4),
                FAKER.number().numberBetween(0, 3),
                FAKER.internet().url());
        nextInteraction.setScheduled(now.plusDays(2).plusHours(10));
        lead.getInteractions().add(nextInteraction);

        Long PREVIOUS_INTERACTION_ID = FAKER.number().randomNumber();
        LeadInteraction previousInteraction = newLeadInteractionEntity(
                createInteractionKey(lead.getId(), PREVIOUS_INTERACTION_ID),
                FAKER.number().numberBetween(0, 4),
                FAKER.number().numberBetween(0, 3),
                FAKER.internet().url());
        previousInteraction.setScheduled(now.minusDays(2).plusHours(2));
        lead.getInteractions().add(previousInteraction);

        // Set up the mocked repository
        doReturn(Collections.singletonList(clientEntity)).when(clientRepository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertEquals(listSize, returnedList.size(), String.format("Lead list size must be %d", listSize));
        Assertions.assertNotNull(returnedList.get(0).getNext(), "First element in the lead list must have a next interaction");
        Assertions.assertNotNull(returnedList.get(0).getLast(), "First element in the lead list must have a last interaction");
    }

    @Test
    @DisplayName("ClientLead Service: findAllMain when multiple clients found but no leads - Success")
    void findAllMainWhenThereAreClientsButNoLeadsReturnsSuccess() {
        int clientListSize = 3;
        List<Client> clientList = IntStream.range(0, clientListSize)
                .mapToObj((client) -> {
                    Long CLIENT_ID = FAKER.number().randomNumber();
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    return getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);

                }).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(clientList).when(clientRepository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        Assertions.assertEquals(0, returnedList.size(), String.format("Lead list size must be %d", 0));
    }

    @Test
    @DisplayName("ClientLead Service: findClientsMain when client found and there are leads (no next or previous interactions) - Success")
    void findClientsMainWhenThereAreMultipleClientsFoundContainingMultipleLeadsReturnsSuccess() {
        int clientListSize = 3;
        List<Client> clientList = getMockedClientList(clientListSize);
        clientList.forEach( (client) -> {
            List<Lead> leadList = getMockedLeadList(FAKER.random().nextInt(1, 4));
            client.getLeads().addAll(leadList);
        });
        long leadNumber = clientList.stream().mapToLong(client -> client.getLeads().size()).sum();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(clientList).when(clientRepository).findAllById(anyList());

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findClientsMain(anyList(), now);

        Assertions.assertNotNull(returnedList, "The list of leads can not be null");
        Assertions.assertEquals(leadNumber, returnedList.size(), String.format("Number of leads in the list must be %d", leadNumber));
    }

    @Test
    @DisplayName("ClientLead Service: findClientsMain when clients not found - Success")
    void findClientsMainWhenClientsNotFoundReturnsSuccess() {
        int clientListSize = 3;
        List<Client> clientList = getMockedClientList(clientListSize);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(clientList).when(clientRepository).findAllById(anyList());

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findClientsMain(anyList(), now);

        Assertions.assertNotNull(returnedList, "The list of leads can not be null");
        Assertions.assertEquals(0, returnedList.size(), String.format("Number of leads in the list must be %d", 0));
    }

    @Test
    @DisplayName("ClientLead Service: findClientsMain when clients not found - Success")
    void findClientsMainWhenClientsFoundButNoLeadsFoundReturnsSuccess() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Set up the mocked repository
        doReturn(Collections.emptyList()).when(clientRepository).findAllById(anyList());

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findClientsMain(anyList(), now);

        Assertions.assertNotNull(returnedList, "The list of leads can not be null");
        Assertions.assertEquals(0, returnedList.size(), String.format("Number of leads in the list must be %d", 0));
    }

    @Test
    @DisplayName("ClientLead Service: add when client found and lead found - Success")
    void addWhenClientFoundAndLeadFoundReturnsSuccess() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Long LEAD_ID = FAKER.number().randomNumber();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        Lead leadMockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);


        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(Optional.of(leadMockedEntity)).when(leadServiceUtils).findById(anyLong());

        // Execute the service call
        LeadBasicDTO returnedEntity = service.add(CLIENT_ID, LEAD_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Lead entity can not be null");
        Assertions.assertEquals(1, spyClientEntity.getLeads().size(), String.format("Client lead size must be %d", 1));
        Assertions.assertNotEquals(DomainConstants.ClientStatus.ON_BOARDING, spyClientEntity.getStatus().getValue(), String.format("Client status can not %s", DomainConstants.ClientStatus.ON_BOARDING));

        verify(spyClientEntity, Mockito.times(1)).setUpdated(any());
    }

    @Test
    @DisplayName("ClientLead Service: add when client found and lead not found - Failure")
    void addWhenClientFoundAndLeadNotFoundReturnsResourceNotFoundException() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Long LEAD_ID = FAKER.number().randomNumber();
        Client mockClientEntity = getMockFactory().newClientEntity(CLIENT_ID);


        // Set up the mocked repository
        doReturn(Optional.of(mockClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(Optional.empty()).when(leadServiceUtils).findById(anyLong());

        // Asserts and execute the call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.add(CLIENT_ID, LEAD_ID),
                "create must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);

        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");
    }

    @Test
    @DisplayName("ClientLead Service: remove when client found and lead found - Success")
    void removeWhenClientFoundAndLeadFoundReturnsSuccess() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Long LEAD_ID = FAKER.number().randomNumber();
        Lead leadMockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        spyClientEntity.getLeads().add(leadMockedEntity);

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(Optional.of(leadMockedEntity)).when(leadServiceUtils).findById(anyLong());

        // Execute the service call
        LeadBasicDTO returnedEntity = service.remove(CLIENT_ID, LEAD_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Lead entity can not be null");
        Assertions.assertEquals(0, spyClientEntity.getLeads().size(), String.format("Client lead size must be %d", 0));

        verify(spyClientEntity, Mockito.times(1)).setUpdated(any());
    }

    @Test
    @DisplayName("ClientLead Service: remove when client found and lead found - Success")
    void removeWhenClientFoundAndLeadNotFoundReturnsSuccess() {
        Long CLIENT_ID = FAKER.number().randomNumber();
        Long LEAD_ID = FAKER.number().randomNumber();
        Client mockClientEntity = getMockFactory().newClientEntity(CLIENT_ID);


        // Set up the mocked repository
        doReturn(Optional.of(mockClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(Optional.empty()).when(leadServiceUtils).findById(anyLong());

        // Asserts and execute the call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.remove(CLIENT_ID, LEAD_ID),
                "create must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);

        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");
    }

    private LeadInteraction newLeadInteractionEntity(Key key, int status, int type, String urlInvite) {
        LeadInteraction mockedEntity = new LeadInteraction();
        mockedEntity.setKey(key);
        mockedEntity.setStatus(status);
        mockedEntity.setType(type);
        mockedEntity.setUrlInvite(UrlTitle.builder().url(urlInvite).build());
        return mockedEntity;
    }

    private Key createInteractionKey(Long leadId, Long interactionId) {
        Key parentKey = dataStoreKeyFactory.getKeyFactory(DataStoreConstants.LEAD_ENTITY_ENTITY).newKey(leadId);
        return Key.newBuilder(parentKey, DataStoreConstants.LEAD_INTERACTION_ENTITY_NAME, interactionId).build();
    }

    private List<Lead> getMockedLeadList(int listSize) {
        return IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    String LEAD_FULL_NAME = FAKER.name().fullName();
                    String LEAD_LINKED_IN_URL = FAKER.internet().url();
                    String LEAD_LINKED_IN_THREAD_URL = FAKER.internet().url();
                    return getMockFactory().newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_THREAD_URL, true);
                }).collect(Collectors.toList());
    }

    private List<Client> getMockedClientList(int listSize) {
        List<Long> clientIds = IntStream
                .range(0, listSize)
                .mapToObj( num -> FAKER.number().randomNumber()).collect(Collectors.toList());
        return  clientIds
                .stream()
                .map( (clientId) -> {
                    String CLIENT_FULL_NAME = FAKER.name().fullName();
                    String CLIENT_LINKED_IN_URL = FAKER.internet().url();
                    return getMockFactory().newClientEntity(clientId, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
                }).collect(Collectors.toList());
    }
}
