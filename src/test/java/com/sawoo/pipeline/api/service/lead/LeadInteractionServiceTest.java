package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.interaction.InteractionMapper;
import com.sawoo.pipeline.api.service.interaction.InteractionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeadInteractionServiceTest extends BaseLightServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @MockBean
    private InteractionService interactionService;

    @Autowired
    public LeadInteractionServiceTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory, DBConstants.LEAD_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("addInteraction: lead does exist and lead interaction is valid - Success")
    void addInteractionWhenLeadExistsAndLeadInteractionValidSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        InteractionDTO interactionMock = getMockFactory().getInteractionMockFactory().newDTO(null);
        InteractionDTO interactionCreated = getMockFactory().getInteractionMockFactory().newDTO(INTERACTION_ID, interactionMock);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(interactionCreated).when(interactionService).create(any(InteractionDTO.class));
        doReturn(new InteractionMapper()).when(interactionService).getMapper();

        // Execute the service call
        InteractionDTO returnedDTO = getService().addInteraction(LEAD_ID, interactionCreated);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new interaction", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead interaction can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Interaction id must be [%s]", INTERACTION_ID)));

        Assertions.assertFalse(
                spyLeadEntity.getInteractions().isEmpty(),
                String.format("Interaction list can not be empty for lead id [%s]", LEAD_ID));

        verify(spyLeadEntity, atLeast(1)).getInteractions();
        verify(spyLeadEntity, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("addInteraction: lead does not exist and interaction on is valid - Failure")
    void addInteractionWhenLeadDoesNotExistAndInteractionValidFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        InteractionDTO interactionToBeCreated = getMockFactory().getInteractionMockFactory().newDTO(null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("addInteraction: lead does exist and interaction not valid - Failure")
    void addInteractionWhenLeadDoesExistAndInteractionNotValidFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        InteractionDTO interactionToBeCreated = getMockFactory().getInteractionMockFactory().newDTO(null);
        interactionToBeCreated.setScheduled(null);

        // Asserts
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> getService().addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("addInteraction: lead does exist and interaction is valid - Failure")
    void addInteractionWhenLeadDoesExistAndInteractionAlreadyScheduledFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String LEAD_INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        InteractionDTO interactionToBeCreated = getMockFactory().getInteractionMockFactory().newDTO(null);
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Interaction interaction = getMockFactory().getInteractionMockFactory().newEntity(LEAD_INTERACTION_ID);
        interaction.setScheduled(interactionToBeCreated.getScheduled());
        spyLeadEntity.getInteractions().add(interaction);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());

        // Asserts
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().addInteraction(LEAD_ID, interactionToBeCreated),
                "addInteraction must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("removeInteraction: lead does exist and lead interaction found - Success")
    void removeInteractionWhenLeadExistsAndLeadInteractionFoundSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Interaction interaction = getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
        spyLeadEntity.getInteractions().add(interaction);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(new InteractionMapper()).when(interactionService).getMapper();

        // Execute the service call
        InteractionDTO returnedDTO = getService().removeInteraction(LEAD_ID, INTERACTION_ID);

        Assertions.assertAll(String.format("Lead id [%s] must be updated with a new interaction", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead interaction can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Interaction id must be [%s]", INTERACTION_ID)));

        Assertions.assertTrue(
                spyLeadEntity.getInteractions().isEmpty(),
                String.format("Interaction list must be empty for lead id [%s]", LEAD_ID));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("removeInteraction: lead does not exist - Failure")
    void removeInteractionWhenLeadDoesNotExistFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().removeInteraction(LEAD_ID, INTERACTION_ID),
                "removeInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("removeInteraction: interaction id is null - Failure")
    void removeInteractionWhenInteractionIdNullFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Asserts
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> getService().removeInteraction(LEAD_ID, null),
                "removeInteraction must throw a ConstraintViolationException");

        String exceptionMessage = exception.getMessage();
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exceptionMessage));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());

        verify(repository, never()).findById(anyString());
    }
}
