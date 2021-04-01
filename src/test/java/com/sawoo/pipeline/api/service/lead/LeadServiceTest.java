package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
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
class LeadServiceTest extends BaseServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @Autowired
    public LeadServiceTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory, DBConstants.LEAD_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Lead component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(LeadDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Lead entity) {
        doReturn(Optional.of(entity)).when(repository).findById(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        LeadDTO mockedDTO = getMockFactory().newDTO(LEAD_ID);
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        String PERSON_ID = getMockFactory().getPersonMockFactory().getComponentId();
        leadEntity.getPerson().setId(PERSON_ID);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(LEAD_ID);
        doReturn(leadEntity).when(repository).insert(any(Lead.class));

        // Execute the service call
        LeadDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating lead with id [[%s] must return the proper entity", LEAD_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_ID,
                        returnedEntity.getId(),
                        String.format("Lead id must be [%s]", LEAD_ID)),
                () -> Assertions.assertEquals(
                        PERSON_ID,
                        returnedEntity.getPerson().getId(),
                        String.format("Person id must be [%s]", PERSON_ID)));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).insert(any(Lead.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String LEAD_LINKED_IN_CHAT_URL = getMockFactory().getFAKER().internet().url();
        LeadDTO mockedDTOTOUpdate = new LeadDTO();
        mockedDTOTOUpdate.setLinkedInThread(LEAD_LINKED_IN_CHAT_URL);
        mockedDTOTOUpdate.setStatus(Status
                .builder()
                .value(LeadStatusList.DEAD.getStatus()).build());
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(LEAD_ID);

        // Execute the service call
        LeadDTO returnedDTO = getService().update(LEAD_ID, mockedDTOTOUpdate);

        // Assertions and verifications
        Assertions.assertAll(String.format("Lead entity with id [%s] must be properly updated", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead entity can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_LINKED_IN_CHAT_URL,
                        returnedDTO.getLinkedInThread(),
                        String.format("LinkedIn Chat Url must be '%s'", LEAD_LINKED_IN_CHAT_URL)));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("deleteLeadSummary: entity does exist - Success")
    void deleteLeadSummaryWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());

        // Execute the service call
        LeadDTO returnedDTO = getService().deleteLeadSummary(LEAD_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Lead DTO can not be null");
        Assertions.assertNull(returnedDTO.getLeadNotes(), "Lead notes must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadSummary: entity does not exist - Failure")
    void deleteLeadSummaryWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteLeadSummary(LEAD_ID),
                "deleteLeadSummary must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadSummary: lead id invalid (empty string) - Failure")
    void deleteLeadSummaryWhenLeadIdInvalidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteLeadSummary(LEAD_ID),
                "deleteLeadSummary must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadCompanyComments: entity does exist - Success")
    void deleteLeadCompanyCommentsWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());

        // Execute the service call
        LeadDTO returnedDTO = getService().deleteLeadCompanyComments(LEAD_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Lead DTO can not be null");
        Assertions.assertNull(returnedDTO.getCompanyNotes(), "Lead company comments must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadCompanyComments: entity does not exist - Failure")
    void deleteLeadCompanyCommentsWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteLeadCompanyComments(LEAD_ID),
                "deleteLeadCompanyComments must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadCompanyComments: lead id invalid (empty string) - Failure")
    void deleteLeadCompanyCommentsWhenLeadIdInvalidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteLeadCompanyComments(LEAD_ID),
                "deleteLeadCompanyComments must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadQualificationComments: entity does exist - Success")
    void deleteLeadQualificationCommentsWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());

        // Execute the service call
        LeadDTO returnedDTO = getService().deleteLeadQualificationComments(LEAD_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Lead DTO can not be null");
        Assertions.assertNotNull(returnedDTO.getStatus(), "Lead status can not be null");
        Assertions.assertNull(returnedDTO.getStatus().getNotes(), "Lead qualification comments must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadQualificationComments: entity does not exist - Failure")
    void deleteLeadQualificationCommentsWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteLeadQualificationComments(LEAD_ID),
                "deleteLeadQualificationComments must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("deleteLeadQualificationComments: lead id invalid (empty string) - Failure")
    void deleteLeadQualificationCommentsWhenLeadIdInvalidReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        LeadService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteLeadQualificationComments(LEAD_ID),
                "deleteLeadQualificationComments must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }
}
