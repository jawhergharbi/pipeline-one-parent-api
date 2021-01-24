package com.sawoo.pipeline.api.service.interaction;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.mock.InteractionMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InteractionServiceTest extends BaseServiceTest<InteractionDTO, Interaction, InteractionRepository, InteractionService, InteractionMockFactory> {

    @MockBean
    private InteractionRepository repository;

    @Autowired
    public InteractionServiceTest(InteractionMockFactory mockFactory, InteractionService service) {
        super(mockFactory, DBConstants.INTERACTION_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Interaction component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(InteractionDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Interaction entity) {
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
        String LEAD_INTERACTION_ID = getMockFactory().getComponentId();
        InteractionDTO mockedDTO = getMockFactory().newDTO(null);
        Interaction interaction = getMockFactory().newEntity(LEAD_INTERACTION_ID);

        // Set up the mocked repository
        doReturn(interaction).when(repository).insert(any(Interaction.class));

        // Execute the service call
        InteractionDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating lead interaction with id [[%s] must return the proper entity", LEAD_INTERACTION_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_INTERACTION_ID,
                        returnedEntity.getId(),
                        String.format("Lead interaction id must be [%s]", LEAD_INTERACTION_ID)));

        verify(repository, never()).findById(anyString());
        verify(repository, times(1)).insert(any(Interaction.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_INTERACTION_ID = getMockFactory().getComponentId();
        InteractionDTO mockedDTOTOUpdate = new InteractionDTO();
        mockedDTOTOUpdate.setScheduled(LocalDateTime.now(ZoneOffset.UTC));
        Interaction interactionEntity = getMockFactory().newEntity(LEAD_INTERACTION_ID);

        // Set up the mocked repository
        doReturn(Optional.of(interactionEntity)).when(repository).findById(LEAD_INTERACTION_ID);

        // Execute the service call
        InteractionDTO returnedDTO = getService().update(LEAD_INTERACTION_ID, mockedDTOTOUpdate);

        Assertions.assertAll(String.format("Lead interaction entity with id [%s] must be properly updated", LEAD_INTERACTION_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead interaction entity can not be null"),
                () -> Assertions.assertEquals(
                        LocalDateTime.now(ZoneOffset.UTC).getDayOfMonth(),
                        returnedDTO.getScheduled().getDayOfMonth(),
                        "Scheduled date must be today"));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }
}
