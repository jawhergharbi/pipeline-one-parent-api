package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.mock.InteractionMockFactory;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class InteractionRepositoryTest extends BaseRepositoryTest<Interaction, InteractionRepository, InteractionMockFactory> {

    private static final String INTERACTION_JSON_DATA_FILE_NAME = "interaction-test-data.json";
    private static final String INTERACTION_ID = "5f4d6a36545bd64a36";
    private static final String COMPONENT_ID_1 = "5fa3c963da6ra335fa2s323d45b";
    private static final String COMPONENT_ID_2 = "a335f5236ra2fda33s323c96d45b";


    @Autowired
    public InteractionRepositoryTest(
            InteractionRepository repository,
            InteractionMockFactory mockFactory) {
        super(repository, INTERACTION_JSON_DATA_FILE_NAME, INTERACTION_ID, Interaction.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Interaction[]> getClazz() {
        return Interaction[].class;
    }

    @Override
    protected String getComponentId(Interaction component) {
        return component.getId();
    }

    @Override
    protected Interaction getNewEntity() {
        String LEAD_INTERACTION_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(LEAD_INTERACTION_ID);
    }

    @Test
    @DisplayName("findByLeadId: entities found - Success")
    void findByLeadIdWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 3;

        // Execute query
        List<Interaction> interactions = getRepository().findByComponentId(COMPONENT_ID_1);

        // Assertions
        Assertions.assertAll(String.format("Interactions for lead with [%s]", COMPONENT_ID_1),
                () -> Assertions.assertFalse(interactions.isEmpty(), "List of interactions can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTIONS_SIZE,
                        interactions.size(),
                        String.format("Interactions with lead id [%s] must be [%d]", COMPONENT_ID_1, INTERACTIONS_SIZE)),
                () -> Assertions.assertEquals(
                        COMPONENT_ID_1,
                        interactions.get(0).getComponentId(),
                        String.format("Lead id must be [%s]", COMPONENT_ID_1)));
    }

    @Test
    @DisplayName("findByLeadId: entities found - Success")
    void findByLeadIdInWhenEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 5;
        List<String> LEAD_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Interaction> interactions = getRepository().findByComponentIdIn(LEAD_IDS);

        // Assertions
        Assertions.assertAll(String.format("Interactions for lead with ids [%s]", LEAD_IDS.toArray()),
                () -> Assertions.assertFalse(interactions.isEmpty(), "List of interactions can not be null"),
                () -> Assertions.assertEquals(
                        INTERACTIONS_SIZE,
                        interactions.size(),
                        String.format("Interactions with lead id [%s] must be [%d]", COMPONENT_ID_1, INTERACTIONS_SIZE)));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusTypeAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 1;
        int STATUS = 1;
        int TYPE = 0;

        // Execute query
        List<Interaction> interactions = getRepository().findBy(STATUS, TYPE, Collections.singletonList(COMPONENT_ID_1));

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
        List<Interaction> interactions = getRepository().findBy(STATUS, TYPE, Collections.emptyList());

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
        List<Interaction> interactions = getRepository().findBy(STATUS, TYPE, null);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByWhenStatusListTypeListAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 5;
        List<String> LEAD_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Interaction> interactions = getRepository().findBy(null, null, LEAD_IDS);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByStatusAndTypeWhenStatusListNullTypeListNullAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 5;
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Interaction> interactions = getRepository().findByStatusAndType(null, null, COMPONENT_IDS);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findBy: filter by status, type, leadIds and entities found - Success")
    void findByStatusAndTypeWhenStatusListTypeListAndLeadIdsAndEntitiesFoundReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 3;
        List<Integer> types = Collections.singletonList(1);
        List<String> COMPONENT_IDS = Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2);

        // Execute query
        List<Interaction> interactions = getRepository().findByStatusAndType(null, types, COMPONENT_IDS);

        Assertions.assertFalse(interactions.isEmpty(), "Interactions can not be empty");
        Assertions.assertEquals(INTERACTIONS_SIZE, interactions.size(), String.format("Interactions size must be [%d]", INTERACTIONS_SIZE));
    }

    @Test
    @DisplayName("findByAssigneeId: filter interactions by userId - Success")
    void findByAssigneeIdWhenUserIdDoesExistReturnsSuccess() {
        // Assign
        int INTERACTIONS_SIZE = 3;
        String ASSIGNEE_ID = "5fa317cd0efe4d20ad3edd13";

        // Execute query
        List<Interaction> interactions = getRepository().findByAssigneeId(ASSIGNEE_ID);

        Assertions.assertAll(String.format("Interactions filter by userId [%s]", ASSIGNEE_ID),
                () -> Assertions.assertFalse(
                        interactions.isEmpty(),
                        "Interactions can not be empty"),
                () -> Assertions.assertEquals(
                        INTERACTIONS_SIZE,
                        interactions.size(),
                        String.format("Interactions size must be [%d]", INTERACTIONS_SIZE)));
    }

    @Test
    @DisplayName("findByAssigneeId: filter interactions by userId not interactions found- Success")
    void findByAssigneeIdWhenUserIdDoesNotExistReturnsSuccess() {
        // Assign
        String ASSIGNEE_ID = "wrongId";

        // Execute query
        List<Interaction> interactions = getRepository().findByAssigneeId(ASSIGNEE_ID);

        Assertions.assertAll(String.format("Interactions filter by assingeeId [%s]", ASSIGNEE_ID),
                () -> Assertions.assertTrue(
                        interactions.isEmpty(),
                        "Interactions must be empty"));
    }
}
