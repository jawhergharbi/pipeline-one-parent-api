package com.sawoo.pipeline.api.service.leadinteraction;

import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.mock.LeadInteractionMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.leadinteraction.LeadInteractionRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeadInteractionServiceTest extends BaseServiceTest<LeadInteractionDTO, LeadInteraction, LeadInteractionRepository, LeadInteractionService, LeadInteractionMockFactory> {

    @MockBean
    private LeadInteractionRepository repository;

    @Autowired
    public LeadInteractionServiceTest(LeadInteractionMockFactory mockFactory, LeadInteractionService service) {
        super(mockFactory, DBConstants.LEAD_INTERACTION_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(LeadInteraction component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(LeadInteractionDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(LeadInteraction entity) {
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
        LeadInteractionDTO mockedDTO = getMockFactory().newDTO(null);
        LeadInteraction leadInteraction = getMockFactory().newEntity(LEAD_INTERACTION_ID);

        // Set up the mocked repository
        doReturn(leadInteraction).when(repository).insert(any(LeadInteraction.class));

        // Execute the service call
        LeadInteractionDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertAll(String.format("Creating lead interaction with id [[%s] must return the proper entity", LEAD_INTERACTION_ID),
                () -> Assertions.assertNotNull(returnedEntity, "Entity can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_INTERACTION_ID,
                        returnedEntity.getId(),
                        String.format("Lead interaction id must be [%s]", LEAD_INTERACTION_ID)));

        verify(repository, never()).findById(anyString());
        verify(repository, times(1)).insert(any(LeadInteraction.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_INTERACTION_ID = getMockFactory().getComponentId();
        LeadInteractionDTO mockedDTOTOUpdate = new LeadInteractionDTO();
        mockedDTOTOUpdate.setScheduled(LocalDateTime.now(ZoneOffset.UTC));
        LeadInteraction leadInteractionEntity = getMockFactory().newEntity(LEAD_INTERACTION_ID);

        // Set up the mocked repository
        doReturn(Optional.of(leadInteractionEntity)).when(repository).findById(LEAD_INTERACTION_ID);

        // Execute the service call
        LeadInteractionDTO returnedDTO = getService().update(LEAD_INTERACTION_ID, mockedDTOTOUpdate);

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
