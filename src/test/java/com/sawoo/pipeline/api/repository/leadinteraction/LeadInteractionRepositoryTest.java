package com.sawoo.pipeline.api.repository.leadinteraction;

import com.sawoo.pipeline.api.mock.LeadInteractionMockFactory;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.BaseRepositoryTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadInteractionRepositoryTest extends BaseRepositoryTest<LeadInteraction, LeadInteractionRepository, LeadInteractionMockFactory> {

    private static final File LEAD_INTERACTION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "lead-interaction-test-data.json").toFile();
    private static final String LEAD_INTERACTION_ID = "5f4d6a36545bd64a36";
    private static final String LEAD_ID_1 = "5fa3c963da6ra335fa2s323d45b";
    private static final String LEAD_ID_2 = "a335f5236ra2fda33s323c96d45b";


    @Autowired
    public LeadInteractionRepositoryTest(
            LeadInteractionRepository repository,
            LeadInteractionMockFactory mockFactory) {
        super(repository, LEAD_INTERACTION_JSON_DATA, LEAD_INTERACTION_ID, LeadInteraction.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<LeadInteraction[]> getClazz() {
        return LeadInteraction[].class;
    }

    @Override
    protected String getComponentId(LeadInteraction component) {
        return component.getId();
    }

    @Override
    protected LeadInteraction getNewEntity() {
        String LEAD_INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(LEAD_INTERACTION_ID);
    }

    @Test
    @DisplayName("findByLeadId: entities found - Success")
    void findByLeadIdWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 3;

        // Execute query
        List<LeadInteraction> interactions = getRepository().findByLeadId(LEAD_ID_1);

        // Assertions
        Assertions.assertAll(String.format("Interactions for lead with [%s]", LEAD_ID_1),
                () -> Assertions.assertFalse(interactions.isEmpty(), "List of interactions can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTIONS_SIZE,
                        interactions.size(),
                        String.format("Interactions with lead id [%s] must be [%d]", LEAD_ID_1, INTERACTIONS_SIZE)),
                () -> Assertions.assertEquals(
                        LEAD_ID_1,
                        interactions.get(0).getLeadId(),
                        String.format("Lead id must be [%s]", LEAD_ID_1)));
    }

    @Test
    @DisplayName("findByLeadId: entities found - Success")
    void findByLeadIdInWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 4;
        List<String> LEAD_IDS = Arrays.asList(LEAD_ID_1, LEAD_ID_2);

        // Execute query
        List<LeadInteraction> interactions = getRepository().findByLeadIdIn(LEAD_IDS);

        // Assertions
        Assertions.assertAll(String.format("Interactions for lead with ids [%s]", LEAD_IDS.toArray()),
                () -> Assertions.assertFalse(interactions.isEmpty(), "List of interactions can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTIONS_SIZE,
                        interactions.size(),
                        String.format("Interactions with lead id [%s] must be [%d]", LEAD_ID_1, INTERACTIONS_SIZE)));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusTypeAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 1;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<LeadInteraction> interactions = getRepository().findBy(STATUS, TYPE, Collections.singletonList(LEAD_ID_1));

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusTypeAndLeadIdsEmptyAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 2;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<LeadInteraction> interactions = getRepository().findBy(STATUS, TYPE, Collections.emptyList());

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusTypeAndLeadIdsNullAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 2;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<LeadInteraction> interactions = getRepository().findBy(STATUS, TYPE, null);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusListTypeListAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 4;
        List<String> LEAD_IDS = Arrays.asList(LEAD_ID_1, LEAD_ID_2);

        // Execute query
        List<LeadInteraction> interactions = getRepository().findBy(null, null, LEAD_IDS);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByStatusTypeLeadsWhenStatusListTypeListAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 4;
        List<String> LEAD_IDS = Arrays.asList(LEAD_ID_1, LEAD_ID_2);

        // Execute query
        List<LeadInteraction> interactions = getRepository().findByStatusTypeLeads(null, null, LEAD_IDS);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }
}
