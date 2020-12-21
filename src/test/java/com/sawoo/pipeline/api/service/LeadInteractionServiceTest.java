package com.sawoo.pipeline.api.service;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTOOld;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import com.sawoo.pipeline.api.repository.interaction.LeadInteractionRepositoryWrapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LeadInteractionServiceTest extends BaseServiceTestOld {

    @Autowired
    private DataStoreKeyFactory dataStoreKeyFactory;

    @Autowired
    private LeadInteractionService service;

    @MockBean
    private LeadInteractionRepositoryWrapper repository;

    @Test
    @DisplayName("Lead Interaction Service: findById - Success")
    void findByIdWhenLeadDoesExitReturnsSuccess() {
        // Set up mock entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long LEAD_INTERACTION_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        int INTERACTION_STATUS = FAKER.number().numberBetween(0, 4);
        int INTERACTION_CHANNEL = FAKER.number().numberBetween(0, 3);
        String INTERACTION_INVITE_URL = FAKER.internet().url();
        LeadInteractionOld mockedEntity = newMockedEntity(
                LEAD_ID,
                LEAD_INTERACTION_ID,
                INTERACTION_STATUS,
                INTERACTION_CHANNEL,
                INTERACTION_INVITE_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(LEAD_ID, LEAD_INTERACTION_ID);

        // Execute the service call
        LeadInteractionDTOOld returnedEntity = service.findById(LEAD_ID, LEAD_INTERACTION_ID);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "LeadInteraction entity with id " + LEAD_ID + " was not found");
        Assertions.assertEquals(returnedEntity.getId(), LEAD_INTERACTION_ID, "LeadInteraction.id should be the same");

        verify(repository, Mockito.times(1)).findById(any(), any());
    }

    private LeadInteractionOld newMockedEntity(Long leadId, Long id, int status, int type, String urlInvite) {
        LeadInteractionOld mockedEntity = new LeadInteractionOld();
        mockedEntity.setKey(createKey(leadId, id));
        mockedEntity.setStatus(status);
        mockedEntity.setType(type);
        mockedEntity.setInvite(UrlTitle.builder().url(urlInvite).build());
        return mockedEntity;
    }

    private Key createKey(Long leadId, Long interactionId) {
        Key parentKey = dataStoreKeyFactory.getKeyFactory(DBConstants.LEAD_DOCUMENT).newKey(leadId);
        return Key.newBuilder(parentKey, DBConstants.LEAD_ACTION_DOCUMENT, interactionId).build();
    }

}
