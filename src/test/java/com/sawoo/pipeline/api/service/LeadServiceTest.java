package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectType;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.LeadOld;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.repository.LeadRepositoryOld;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LeadServiceTest extends BaseServiceTestOld {

    @Autowired
    private LeadServiceOld service;

    @MockBean
    private LeadRepositoryOld repository;

    @SpyBean
    private CompanyService companyService;

    @Test
    @DisplayName("Lead Service: findAllMain - Success")
    void findAllMainWhenThereAreThreeLeadsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<LeadOld> leadList = IntStream.range(0, listSize)
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

        LeadDTOOld mockedDTO = getMockFactory().newLeadDTO(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedDTO.setCompany(CompanyDTO
                .builder()
                .name(COMPANY_NAME)
                .url(COMPANY_URL).build());

        LeadOld mockedEntity = getMockFactory()
                .newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL, false);
        mockedEntity.setCompany(getMockFactory().newCompanyEntity(FAKER.internet().uuid(), COMPANY_NAME, COMPANY_URL, now));
        mockedEntity.setStatus(
                Status.builder()
                        .value(LeadStatusList.HOT.getStatus())
                        .updated(now)
                        .build());

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(LEAD_LINKED_IN_URL);
        doReturn(mockedEntity).when(repository).save(any());
        doReturn(Optional.empty()).when(companyService).findByName(COMPANY_NAME);


        // Execute the service call
        LeadDTOOld returnedEntity = service.create(mockedDTO, ProspectType.LEAD.getType());

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
    @DisplayName("Lead service: create when lead does exist - Failure")
    void createWhenLeadDoesExistReturnsCommonException() {
        // Set up mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String LEAD_FIRST_NAME = FAKER.name().firstName();
        String LEAD_LAST_NAME = FAKER.name().lastName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_IN_CHAT_URL = FAKER.internet().url();
        LeadDTOOld mockedDTO = new LeadDTOOld();
        LeadOld mockedEntity = getMockFactory().newLeadEntity(LEAD_ID, LEAD_FIRST_NAME, LEAD_LAST_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_IN_CHAT_URL, true);

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
}
