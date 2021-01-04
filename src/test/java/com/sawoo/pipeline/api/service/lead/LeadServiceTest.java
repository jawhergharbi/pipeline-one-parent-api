package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeadServiceTest extends BaseServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

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
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();
        leadEntity.getProspect().setId(PROSPECT_ID);

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
                        PROSPECT_ID,
                        returnedEntity.getProspect().getId(),
                        String.format("Prospect id must be [%s]", PROSPECT_ID)));

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

        Assertions.assertAll(String.format("Lead entity with id [%s] must be properly updated", LEAD_ID),
                () -> Assertions.assertNotNull(returnedDTO, "Lead entity can not be null"),
                () -> Assertions.assertEquals(
                        LEAD_LINKED_IN_CHAT_URL,
                        returnedDTO.getLinkedInThread(),
                        String.format("LinkedIn Chat Url must be '%s'", LEAD_LINKED_IN_CHAT_URL)));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any());
    }
}
