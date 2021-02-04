package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMostOnce;
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
public class LeadInteractionServiceTest extends BaseLightServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @MockBean
    private InteractionService interactionService;

    @MockBean
    private LeadInteractionServiceDecoratorHelper helper;

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
    void addInteractionWhenLeadExistsAndLeadInteractionValidReturnsSuccess() {
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
    void addInteractionWhenLeadDoesNotExistAndInteractionValidReturnsFailure() {
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
    void addInteractionWhenLeadDoesExistAndInteractionNotValidReturnsFailure() {
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
    void addInteractionWhenLeadDoesExistAndInteractionAlreadyScheduledReturnsFailure() {
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
    void removeInteractionWhenLeadExistsAndLeadInteractionFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();
        Lead spyLeadEntity = spy(getMockFactory().newEntity(LEAD_ID));
        Interaction interaction = getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
        spyLeadEntity.getInteractions().add(interaction);
        InteractionDTO interactionDTO = (new InteractionMapper()).getMapperOut().getDestination(interaction);

        // Set up the mocked repository
        doReturn(Optional.of(spyLeadEntity)).when(repository).findById(anyString());
        doReturn(interactionDTO).when(interactionService).delete(anyString());

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
    void removeInteractionWhenLeadDoesNotExistReturnsFailure() {
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
    void removeInteractionWhenInteractionIdNullReturnsFailure() {
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

    @Test
    @DisplayName("getInteractions: lead found - Success")
    void getInteractionsWhenLeadFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Interaction> interactionList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setInteractions(interactionList);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new InteractionMapper()).when(interactionService).getMapper();
        doReturn(Collections.emptyList()).when(helper).getUsers(anyString());

        // Execute the service call
        List<InteractionAssigneeDTO> returnedListDTO = getService().getInteractions(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of interactions", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Interaction list can not be empty"),
                () -> Assertions.assertEquals(
                        INTERACTION_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Interaction list size must be [%d]", INTERACTION_LIST_SIZE)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getInteractions: lead found - Success")
    void getInteractionsWhenLeadFoundAndAccountFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 10;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<UserCommon> mockedUsers = IntStream
                .range(0, 2)
                .mapToObj( (u) -> {
                    String USER_FULL_NAME = getMockFactory().getFAKER().name().fullName();
                    String USER_ID = getMockFactory().getFAKER().internet().uuid();
                    return UserCommon.builder().fullName(USER_FULL_NAME).id(USER_ID).build();
                }).collect(Collectors.toList());

        List<Interaction> interactionList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    Interaction interaction = getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
                    interaction.setAssigneeId(mockedUsers
                            .get(getMockFactory()
                                    .getFAKER()
                                    .random()
                                    .nextInt(1)).getId());
                    return interaction;
                }).collect(Collectors.toList());
        leadEntity.setInteractions(interactionList);

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new InteractionMapper()).when(interactionService).getMapper();
        doReturn(mockedUsers).when(helper).getUsers(anyString());

        // Execute the service call
        List<InteractionAssigneeDTO> returnedListDTO = getService().getInteractions(LEAD_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] has a list of interactions", LEAD_ID),
                () -> Assertions.assertFalse(returnedListDTO.isEmpty(), "Interaction list can not be empty"),
                () -> Assertions.assertEquals(
                        INTERACTION_LIST_SIZE,
                        returnedListDTO.size(),
                        String.format("Interaction list size must be [%d]", INTERACTION_LIST_SIZE)),
                () -> Assertions.assertNotNull(returnedListDTO.get(0).getAssignee(), "Assignee can not be null"));

        verify(repository, atMostOnce()).findById(anyString());
        verify(helper, atMostOnce()).getUsers(anyString());
    }

    @Test
    @DisplayName("getInteractions: lead found - Failure")
    void getInteractionsWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().getInteractions(LEAD_ID),
                "getInteractions must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getInteractions: lead and interaction found - Success")
    void getInteractionWhenLeadAndInteractionFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Interaction> interactionList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setInteractions(interactionList);
        int INTERACTION_IDX = new Random().nextInt(3);
        String TARGET_INTERACTION_ID = interactionList.get(INTERACTION_IDX).getId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new InteractionMapper()).when(interactionService).getMapper();

        // Execute the service call
        InteractionDTO returnedDTO = getService().getInteraction(LEAD_ID, TARGET_INTERACTION_ID);

        // Assertions
        Assertions.assertAll(String.format("Lead with id [%s] contains the searched interaction", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Interaction can not be null"),
                () -> Assertions.assertEquals(
                        TARGET_INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Interaction id must be [%s]", TARGET_INTERACTION_ID)));
    }

    @Test
    @DisplayName("getInteraction: lead not found - Success")
    void getInteractionWhenLeadNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().getInteraction(LEAD_ID, INTERACTION_ID),
                "getInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getInteraction: lead found and interaction not found - Success")
    void getInteractionWhenLeadFoundAndInteractionNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        int INTERACTION_LIST_SIZE = 3;
        Lead leadEntity = getMockFactory().newEntity(LEAD_ID);
        List<Interaction> interactionList = IntStream
                .range(0, INTERACTION_LIST_SIZE)
                .mapToObj( (i) -> {
                    String INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().getInteractionMockFactory().newEntity(INTERACTION_ID);
                }).collect(Collectors.toList());
        leadEntity.setInteractions(interactionList);
        String TARGET_INTERACTION_ID = getMockFactory().getInteractionMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.of(leadEntity)).when(repository).findById(anyString());
        doReturn(new InteractionMapper()).when(interactionService).getMapper();

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().getInteraction(LEAD_ID, TARGET_INTERACTION_ID),
                "getInteraction must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }
}
