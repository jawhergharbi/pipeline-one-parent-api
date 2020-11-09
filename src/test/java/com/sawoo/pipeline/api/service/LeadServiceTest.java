package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectType;
import com.sawoo.pipeline.api.model.Status;
import com.sawoo.pipeline.api.model.prospect.Lead;
import com.sawoo.pipeline.api.model.prospect.ProspectStatus;
import com.sawoo.pipeline.api.repository.LeadRepository;
import com.sawoo.pipeline.api.service.company.CompanyService;
import org.junit.jupiter.api.*;
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
        LeadDTO returnedEntity = service.findById(LEAD_ID);

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
        List<LeadDTO> returnedList = service.findAll();

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
        List<LeadDTO> returnedList = service.findAll();

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
    @DisplayName("Lead Service: delete lead entity found - Success")
    void deleteWhenCompanyEntityFoundReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_FULL_NAME = String.join(" ", LEAD_FIRST_NAME, LEAD_LAST_NAME);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadDTO> returnedDTO = service.delete(LEAD_ID);

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
        Optional<LeadDTO> returnedEntity = service.delete(LEAD_ID);

        Assertions.assertFalse(returnedEntity.isPresent(), "Returned entity must be null");

        verify(repository, times(1)).findById(LEAD_ID);
    }

    @Test
    @DisplayName("Lead Service: create when lead does not exist - Success")
    void createWhenLeadDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_FULL_NAME = String.join(" ", LEAD_FIRST_NAME, LEAD_LAST_NAME);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadDTO mockedDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(FAKER.internet().uuid(), COMPANY_NAME, COMPANY_URL, now));
        mockedEntity.setStatus(
                Status.builder()
                        .value(ProspectStatus.HOT.getStatus())
                        .updated(now)
                        .build());

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(LEAD_LINKED_IN_URL);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.empty()).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        LeadDTO returnedEntity = service.create(mockedDTO, ProspectType.LEAD.getType());

        // Assertions
        Assertions.assertNotNull(returnedEntity, String.format("Lead entity with name [%s] was found already in the system", LEAD_FULL_NAME));
        Assertions.assertEquals(LEAD_FULL_NAME, returnedEntity.getFullName(), "Lead.fullName should be the same");
        Assertions.assertNotNull(returnedEntity.getStatus(), "Lead.status can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Updated time must be today");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByLinkedInUrl(LEAD_LINKED_IN_URL);
        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(companyNameCaptor.capture());

        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));
    }

    @Test
    @DisplayName("Lead Service: create when lead does not exist but company exists - Success")
    void createWhenLeadDoesNotExistAndCompanyDoesExistReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_FULL_NAME = String.join(" ", LEAD_FIRST_NAME, LEAD_LAST_NAME);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadDTO mockedDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());

        String EXISTING_COMPANY_ID = FAKER.internet().uuid();
        LocalDateTime EXISTING_COMPANY_DATETIME = LocalDateTime.of(2020, 12, 31, 12, 0);
        CompanyDTO existingCompanyDTO = getMockFactory().newCompanyDTO(EXISTING_COMPANY_ID, COMPANY_NAME, COMPANY_URL, EXISTING_COMPANY_DATETIME);

        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(EXISTING_COMPANY_ID, COMPANY_NAME, COMPANY_URL, EXISTING_COMPANY_DATETIME));

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(LEAD_LINKED_THREAD_URL);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.of(existingCompanyDTO)).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        LeadDTO returnedEntity = service.create(mockedDTO, ProspectType.LEAD.getType());

        // Assertions
        Assertions.assertNotNull(returnedEntity, String.format("Lead entity with name [%s] was found already in the system", LEAD_FULL_NAME));
        Assertions.assertEquals(LEAD_FULL_NAME, returnedEntity.getFullName(), "Lead.fullName should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Updated time must be today");
        Assertions.assertNotEquals(now, returnedEntity.getCompany().getCreated(), "Company creation time can not be now");

        verify(repository, times(1)).save(any());
        verify(repository, times(1)).findByLinkedInUrl(LEAD_LINKED_THREAD_URL);
        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(companyService, times(1)).findByName(companyNameCaptor.capture());

        Assertions.assertEquals(companyNameCaptor.getValue(), COMPANY_NAME, String.format("Company name to be verified must be: [%s]", COMPANY_NAME));
    }

    @Test
    @DisplayName("Lead service: create when lead does exist - Failure")
    void createWhenLeadDoesExistReturnsCommonException() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_IN_CHAT_URL = FAKER.internet().url();
        LeadDTO mockedDTO = new LeadDTO();
        Lead mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_CHAT_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByLinkedInUrl(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(mockedDTO, ProspectType.LEAD.getType()),
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
        verify(repository, times(1)).findByLinkedInUrl(fullNameCaptor.capture());
        Assertions.assertEquals(fullNameCaptor.getValue(), LEAD_LINKED_IN_URL, String.format("Lead [linkedInUrl] to be verified must be: [%s]", LEAD_LINKED_IN_URL));
    }

    @Test
    @DisplayName("Lead Service: update lead name and linkedIn url when lead does exist - Success")
    void updateWhenLeadEntityIsFoundReturnsSuccess() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_FULL_NAME = String.join(" ", LEAD_FIRST_NAME, LEAD_LAST_NAME);
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        LeadDTO mockedDTO = new LeadDTO();
        mockedDTO.setLinkedInUrl(LEAD_LINKED_IN_URL);

        Lead mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, true);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadDTO> returnedDTO = service.update(LEAD_ID, mockedDTO);

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
        LeadDTO mockedDTO = new LeadDTO();
        mockedDTO.setFullName(LEAD_FULL_NAME);
        mockedDTO.setLinkedInUrl(LEAD_LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(LEAD_ID);

        // Execute the service call
        Optional<LeadDTO> returnedEntity = service.update(LEAD_ID, mockedDTO);

        // Assertions
        Assertions.assertFalse(returnedEntity.isPresent(), "Lead entity must be null");

        verify(repository, times(1)).findById(LEAD_ID);
        verify(repository, never()).save(any());
    }
}
