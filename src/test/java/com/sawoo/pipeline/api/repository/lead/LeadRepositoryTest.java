package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.BaseRepositoryTest;
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
public class LeadRepositoryTest extends BaseRepositoryTest<Lead, LeadRepository, LeadMockFactory> {

    private static final File LEAD_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "lead-test-data.json").toFile();
    private static final String LEAD_ID = "5fa3ce63rt4ef23d963da45b";

    @Autowired
    public LeadRepositoryTest(LeadRepository repository, LeadMockFactory mockFactory) {
        super(repository, LEAD_JSON_DATA, LEAD_ID, Lead.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Lead[]> getClazz() {
        return Lead[].class;
    }

    @Override
    protected String getComponentId(Lead component) {
        return component.getId();
    }

    @Override
    protected Lead getNewEntity() {
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(LEAD_ID);
    }
}
