package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeadInteractionServiceTest extends BaseLightServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @MockBean
    private LeadInteractionService leadInteractionService;

    @Autowired
    public LeadInteractionServiceTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory, DBConstants.LEAD_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    void createInteractionWhenLeadEntityFoundAndLeadInteractionValidReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        String LEAD_INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
        Lead spyLead = spy(getMockFactory().newEntity(LEAD_ID));
        LeadInteractionDTO mockedLeadInteractionToCreate = getMockFactory()
                .getLeadInteractionMockFactory()
                .newDTO(null);
        LeadInteractionDTO mockedLeadInteraction = getMockFactory()
                .getLeadInteractionMockFactory()
                .newDTO(LEAD_INTERACTION_ID, mockedLeadInteractionToCreate);


        // Set up the mocked repository
        doReturn(Optional.of(spyLead)).when(repository).findById(anyString());
        doReturn(mockedLeadInteraction).when(leadInteractionService).createInteraction(anyString(), any(LeadInteractionDTO.class));

        // Execute the service call
        LeadInteractionDTO returnedDTO = getService().createInteraction(LEAD_ID, mockedLeadInteractionToCreate);

        // Assertions
        Assertions.assertAll(String.format("Lead interaction added to lead id [%s]", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead interaction can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_INTERACTION_ID,
                        returnedDTO.getId(),
                        String.format("Lead interaction id must be [%s]", LEAD_INTERACTION_ID)));

        Assertions.assertFalse(spyLead.getInteractions().isEmpty(), String.format("Interaction list of the lead id [%s] can not be empty", LEAD_ID));

        verify(spyLead, times(1)).setUpdated(any(LocalDateTime.class));
        verify(repository, times(1)).save(any(Lead.class));
    }
}
