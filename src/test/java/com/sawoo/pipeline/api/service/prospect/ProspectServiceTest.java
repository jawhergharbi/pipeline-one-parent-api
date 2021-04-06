package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.prospect.ProspectStatusList;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
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
class ProspectServiceTest extends BaseServiceTest<ProspectDTO, Prospect, ProspectRepository, ProspectService, ProspectMockFactory> {

    @MockBean
    private ProspectRepository repository;

    @Autowired
    public ProspectServiceTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory, DBConstants.PROSPECT_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Prospect component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(ProspectDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Prospect entity) {
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
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedDTO = getMockFactory().newDTO(PROSPECT_ID);
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
        String PERSON_ID = getMockFactory().getPersonMockFactory().getComponentId();
        prospectEntity.getPerson().setId(PERSON_ID);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(PROSPECT_ID);
        doReturn(prospectEntity).when(repository).insert(any(Prospect.class));

        // Execute the service call
        ProspectDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating prospect with id [[%s] must return the proper entity", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        PROSPECT_ID,
                        returnedEntity.getId(),
                        String.format("Prospect id must be [%s]", PROSPECT_ID)),
                () -> Assertions.assertEquals(
                        PERSON_ID,
                        returnedEntity.getPerson().getId(),
                        String.format("Person id must be [%s]", PERSON_ID)));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).insert(any(Prospect.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String PROSPECT_LINKED_IN_CHAT_URL = getMockFactory().getFAKER().internet().url();
        ProspectDTO mockedDTOTOUpdate = new ProspectDTO();
        mockedDTOTOUpdate.setLinkedInThread(PROSPECT_LINKED_IN_CHAT_URL);
        mockedDTOTOUpdate.setStatus(Status
                .builder()
                .value(ProspectStatusList.DEAD.getStatus()).build());
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(PROSPECT_ID);

        // Execute the service call
        ProspectDTO returnedDTO = getService().update(PROSPECT_ID, mockedDTOTOUpdate);

        // Assertions and verifications
        Assertions.assertAll(String.format("Prospect entity with id [%s] must be properly updated", PROSPECT_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Prospect entity can not be null"),
                () -> Assertions.assertEquals(
                        PROSPECT_LINKED_IN_CHAT_URL,
                        returnedDTO.getLinkedInThread(),
                        String.format("LinkedIn Chat Url must be '%s'", PROSPECT_LINKED_IN_CHAT_URL)));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("deleteProspectSummary: entity does exist - Success")
    void deleteProspectSummaryWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());

        // Execute the service call
        ProspectDTO returnedDTO = getService().deleteProspectSummary(PROSPECT_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Prospect DTO can not be null");
        Assertions.assertNull(returnedDTO.getProspectNotes(), "Prospect notes must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectSummary: entity does not exist - Failure")
    void deleteProspectSummaryWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteProspectSummary(PROSPECT_ID),
                "deleteProspectSummary must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectSummary: prospect id invalid (empty string) - Failure")
    void deleteProspectSummaryWhenProspectIdInvalidReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteProspectSummary(PROSPECT_ID),
                "deleteProspectSummary must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectCompanyComments: entity does exist - Success")
    void deleteProspectCompanyCommentsWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());

        // Execute the service call
        ProspectDTO returnedDTO = getService().deleteProspectCompanyComments(PROSPECT_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Prospect DTO can not be null");
        Assertions.assertNull(returnedDTO.getCompanyNotes(), "Prospect company comments must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectCompanyComments: entity does not exist - Failure")
    void deleteProspectCompanyCommentsWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteProspectCompanyComments(PROSPECT_ID),
                "deleteProspectCompanyComments must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectCompanyComments: prospect id invalid (empty string) - Failure")
    void deleteProspectCompanyCommentsWhenProspectIdInvalidReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteProspectCompanyComments(PROSPECT_ID),
                "deleteProspectCompanyComments must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectQualificationComments: entity does exist - Success")
    void deleteProspectQualificationCommentsWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(prospectEntity)).when(repository).findById(anyString());

        // Execute the service call
        ProspectDTO returnedDTO = getService().deleteProspectQualificationComments(PROSPECT_ID);

        // Assertions and verifications
        Assertions.assertNotNull(returnedDTO, "Prospect DTO can not be null");
        Assertions.assertNotNull(returnedDTO.getStatus(), "Prospect status can not be null");
        Assertions.assertNull(returnedDTO.getStatus().getNotes(), "Prospect qualification comments must be null");
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectQualificationComments: entity does not exist - Failure")
    void deleteProspectQualificationCommentsWhenEntityFoundReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "wrong _id";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteProspectQualificationComments(PROSPECT_ID),
                "deleteProspectQualificationCommentsWhenEntityFoundReturnsFailure must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }

    @Test
    @DisplayName("deleteProspectQualificationComments: prospect id invalid (empty string) - Failure")
    void deleteProspectQualificationCommentsWhenProspectIdInvalidReturnsFailure() {
        // Set up mocked entities
        String PROSPECT_ID = "";

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ProspectService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.deleteProspectQualificationComments(PROSPECT_ID),
                "deleteProspectQualificationCommentsWhenEntityFoundReturnsFailure must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any(Prospect.class));
    }
}
