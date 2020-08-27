package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.model.Status;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.LeadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LeadServiceTest extends BaseServiceTest {

    @Autowired
    private LeadService service;

    @MockBean
    private LeadRepository repository;

    @SpyBean
    private CompanyService companyService;

    @Test
    @DisplayName("Lead Service: findById - Success")
    void findByIdWhenLeadExitsReturnsSuccess() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        LeadBasicDTO returnedEntity = service.findById(LEAD_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Lead entity with id " + LEAD_ID + " was not found");
        Assertions.assertEquals(returnedEntity.getId(), LEAD_ID, "Lead.id should be the same");

        verify(repository, times(1)).findById(LEAD_ID);
    }

    @Test
    @DisplayName("Lead Service: findById when lead does not exists - Failure")
    void findByIdWhenLeadIsNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(LEAD_ID);

        // Execute service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(LEAD_ID),
                "findById must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(LEAD_ID);
    }

    @Test
    @DisplayName("Lead Service: findAll - Success")
    void findAllWhenThereAreThreeLeadsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<Lead> leadList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    return getMockFactory().newLeadEntity(LEAD_ID, true);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(leadList).when(repository).findAll();

        // Execute the service call
        List<LeadBasicDTO> returnedList = service.findAll();

        // Assertions
        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedList.size(), String.format("Returned list size must be %d", listSize));

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: findAll empty list - Success")
    void findAllWhenThereAreNoLeadsEntitiesReturnsSuccess() {
        // Set up mock entities
        List<Lead> leads = Collections.emptyList();

        // Set up the mocked repository
        doReturn(leads).when(repository).findAll();

        // Execute the service call
        List<LeadBasicDTO> returnedList = service.findAll();

        // Assertions
        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: findAllMain - Success")
    void findAllMainWhenThereAreThreeLeadsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<Lead> leadList = IntStream.range(0, listSize)
                .mapToObj((lead) -> {
                    Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    return getMockFactory().newLeadEntity(LEAD_ID, true);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(leadList).when(repository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertEquals(listSize, returnedList.size(), String.format("Returned list size must be %d", listSize));

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: findAllMain with first and last interaction - Success")
    void findAllMainWhenLeadContainsFirstAndLastInteractionReturnsSuccess() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadInteraction next = new LeadInteraction();
        next.setScheduled(LocalDateTime.now(ZoneOffset.UTC).plusDays(30));
        next.setType(DomainConstants.InteractionType.EMAIL.ordinal());
        mockedEntity.getInteractions().add(next);

        LeadInteraction last = new LeadInteraction();
        last.setScheduled(LocalDateTime.now(ZoneOffset.UTC).minusDays(30));
        last.setType(DomainConstants.InteractionType.LINKED_IN.ordinal());
        mockedEntity.getInteractions().add(last);

        // Set up the mocked repository
        doReturn(Collections.singletonList(mockedEntity)).when(repository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(1, returnedList.size(), "Returned list size must be 1");
        Assertions.assertNotNull(returnedList.get(0).getNext(), "Returned next interaction can not be null");
        Assertions.assertNotNull(returnedList.get(0).getLast(), "Returned last interaction can not be null");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: findAllMain with last interaction - Success")
    void findAllMainWhenLeadContainsOnlyLastInteractionReturnsSuccess() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadInteraction last = new LeadInteraction();
        last.setScheduled(LocalDateTime.now(ZoneOffset.UTC).minusDays(30));
        last.setType(DomainConstants.InteractionType.LINKED_IN.ordinal());
        mockedEntity.getInteractions().add(last);

        // Set up the mocked repository
        doReturn(Collections.singletonList(mockedEntity)).when(repository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(1, returnedList.size(), "Returned list size must be 1");
        Assertions.assertNotNull(returnedList.get(0).getLast(), "Returned last interaction can not be null");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: findAllMain with last interaction - Success")
    void findAllMainWhenLeadContainsOnlyNextInteractionReturnsSuccess() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadInteraction next = new LeadInteraction();
        next.setScheduled(LocalDateTime.now(ZoneOffset.UTC).plusDays(30));
        next.setType(DomainConstants.InteractionType.LINKED_IN.ordinal());
        mockedEntity.getInteractions().add(next);

        // Set up the mocked repository
        doReturn(Collections.singletonList(mockedEntity)).when(repository).findAll();

        // Execute the service call
        List<LeadMainDTO> returnedList = service.findAllMain(now);

        // Assertions
        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(1, returnedList.size(), "Returned list size must be 1");
        Assertions.assertNotNull(returnedList.get(0).getNext(), "Returned next interaction can not be null");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lead Service: delete lead entity found - Success")
    void deleteWhenCompanyEntityFoundReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadBasicDTO> returnedDTO = service.delete(LEAD_ID);

        // Assertions
        Assertions.assertTrue(returnedDTO.isPresent(), "Returned entity can not be null");
        Assertions.assertEquals(LEAD_ID, returnedDTO.get().getId(), "lead.id fields must be the same");
        Assertions.assertEquals(LEAD_FULL_NAME, returnedDTO.get().getFullName(), "lead.fullName fields must be the same");

        verify(repository, times(1)).findById(LEAD_ID);
        verify(repository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Lead Service: delete lead entity not found - Null entity")
    void deleteWhenLeadEntityNotFoundReturnsNullEntity() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadBasicDTO> returnedEntity = service.delete(LEAD_ID);

        Assertions.assertFalse(returnedEntity.isPresent(), "Returned entity must be null");

        verify(repository, times(1)).findById(LEAD_ID);
    }

    @Test
    @DisplayName("Lead Service: create when lead does not exist - Success")
    void createWhenLeadDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadBasicDTO mockedDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(FAKER.number().randomNumber(), COMPANY_NAME, COMPANY_URL, now));
        mockedEntity.setStatus(Status.
                builder()
                .value(DomainConstants.LeadStatus.WARM.ordinal())
                .updated(now)
                .build());

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByFullName(LEAD_FULL_NAME);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.empty()).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        LeadBasicDTO returnedEntity = service.create(mockedDTO);

        // Assertions
        Assertions.assertNotNull(returnedEntity, String.format("Lead entity with name [%s] was found already in the system", LEAD_FULL_NAME));
        Assertions.assertEquals(LEAD_FULL_NAME, returnedEntity.getFullName(), "Lead.fullName should be the same");
        Assertions.assertNotNull(returnedEntity.getStatus(), "Lead.status can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Updated time must be today");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByFullName(LEAD_FULL_NAME);
        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(companyNameCaptor.capture());

        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));
    }

    @Test
    @DisplayName("Lead Service: create when lead does not exist but company exists - Success")
    void createWhenLeadDoesNotExistAndCompanyDoesExistReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadBasicDTO mockedDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());

        Long EXISTING_COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        LocalDateTime EXISTING_COMPANY_DATETIME = LocalDateTime.of(2020, 12, 31, 12, 0);
        CompanyDTO existingCompanyDTO = getMockFactory().newCompanyDTO(EXISTING_COMPANY_ID, COMPANY_NAME, COMPANY_URL, EXISTING_COMPANY_DATETIME);

        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(EXISTING_COMPANY_ID, COMPANY_NAME, COMPANY_URL, EXISTING_COMPANY_DATETIME));

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByFullName(LEAD_FULL_NAME);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.of(existingCompanyDTO)).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        LeadBasicDTO returnedEntity = service.create(mockedDTO);

        // Assertions
        Assertions.assertNotNull(returnedEntity, String.format("Lead entity with name [%s] was found already in the system", LEAD_FULL_NAME));
        Assertions.assertEquals(LEAD_FULL_NAME, returnedEntity.getFullName(), "Lead.fullName should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Updated time must be today");
        Assertions.assertNotEquals(now, returnedEntity.getCompany().getCreated(), "Company creation time can not be now");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByFullName(LEAD_FULL_NAME);
        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(companyNameCaptor.capture());

        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));
    }

    @Test
    @DisplayName("Lead service: create when lead does exist - Failure")
    void createWhenLeadDoesExistReturnsCommonException() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        LeadBasicDTO mockedDTO = new LeadBasicDTO();
        mockedDTO.setFullName(LEAD_FULL_NAME);
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByFullName(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(mockedDTO),
                "create must throw a CommonServiceException");

        // Assertions
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION);
        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");

        ArgumentCaptor<String> fullNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository, times(1)).findByFullName(fullNameCaptor.capture());
        Assertions.assertEquals(fullNameCaptor.getValue(), LEAD_FULL_NAME, String.format("Lead fullName to be verified must be: [%s]", LEAD_FULL_NAME));
    }

    @Test
    @DisplayName("Lead Service: update lead name and linkedIn url when lead does exist - Success")
    void updateWhenLeadEntityIsFoundReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        LeadBasicDTO mockedDTO = new LeadBasicDTO();
        mockedDTO.setFullName(LEAD_FULL_NAME);
        mockedDTO.setLinkedInUrl(LEAD_LINKED_IN_URL);

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadBasicDTO> returnedDTO = service.update(LEAD_ID, mockedDTO);

        // Assertions
        Assertions.assertTrue(returnedDTO.isPresent(), "Lead entity is not null");
        Assertions.assertEquals(
                LEAD_FULL_NAME,
                returnedDTO.get().getFullName(),
                String.format("Full Name must be '%s'", LEAD_FULL_NAME));
        Assertions.assertEquals(
                LEAD_LINKED_IN_URL,
                returnedDTO.get().getLinkedInUrl(),
                String.format("LinkedIn Url must be '%s'", LEAD_LINKED_IN_URL));

        verify(repository, times(1)).findById(LEAD_ID);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Lead Service: update lead when lead does not exist - Failure")
    void updateWhenCompanyEntityNotFoundReturnsFailure() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        LeadBasicDTO mockedDTO = new LeadBasicDTO();
        mockedDTO.setFullName(LEAD_FULL_NAME);
        mockedDTO.setLinkedInUrl(LEAD_LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadBasicDTO> returnedEntity = service.update(LEAD_ID, mockedDTO);

        // Assertions
        Assertions.assertFalse(returnedEntity.isPresent(), "Lead entity must be null");

        verify(repository, times(1)).findById(LEAD_ID);
        verify(repository, never()).save(any());
    }
}
