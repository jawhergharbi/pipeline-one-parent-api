package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.mock.LeadInteractionMockFactory;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.interaction.LeadInteractionRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadInteractionRepositoryTest extends BaseRepositoryTest<LeadInteraction, LeadInteractionRepository, LeadInteractionMockFactory> {

    private static final File LEAD_INTERACTION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "lead-test-data.json").toFile();
    private static final String LEAD_INTERACTION_ID = "5fa3c963da6ra335fa2s323d45b";


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
}
