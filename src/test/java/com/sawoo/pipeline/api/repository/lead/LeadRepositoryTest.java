package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import org.junit.jupiter.api.*;
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
    private static final String LEAD_ID = "5fa3c963da6ra335fa2s323d45b";

    private final ProspectRepository prospectRepository;
    private final CompanyRepository companyRepository;


    @Autowired
    public LeadRepositoryTest(
            LeadRepository repository,
            LeadMockFactory mockFactory,
            ProspectRepository prospectRepository,
            CompanyRepository companyRepository) {
        super(repository, LEAD_JSON_DATA, LEAD_ID, Lead.class.getSimpleName(), mockFactory);
        this.prospectRepository = prospectRepository;
        this.companyRepository = companyRepository;
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

    @AfterEach
    protected void afterEach() {
        super.afterEach();
        prospectRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("save: lead cascade saving - Success")
    void insertWhenProspectDoesNotExistReturnsSuccess() {
        Lead entity = getMockFactory().newEntity(null);

        Lead savedEntity =  getRepository().insert(entity);

        Assertions.assertAll("Prospect reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Lead id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect(),
                        "Prospect entity can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getId(),
                        "Prospect id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getCompany(),
                        "Prospect company can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getCompany().getId(),
                        "Prospect company id can not be null"));
    }

    @Test
    @DisplayName("insert: lead save - Success")
    void insertWhenProspectDoesExistReturnsSuccess() {
        // Arrange
        Lead entity = getMockFactory().newEntity(null);
        Prospect prospectEntity = getMockFactory().getProspectMockFactory().newEntity(null);
        prospectRepository.insert(prospectEntity);
        entity.setProspect(prospectEntity);

        // Act
        Lead savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertAll("Prospect reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Lead id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect(),
                        "Prospect entity can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getId(),
                        "Prospect id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getCompany(),
                        "Prospect company can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getProspect().getCompany().getId(),
                        "Prospect company id can not be null"));
    }

    @Test
    @DisplayName("insert: prospect reference null - Success")
    void insertWhenProspectReferenceNullReturnsSuccess() {
        // Arrange
        Lead entity = getMockFactory().newEntity(null);
        entity.setProspect(null);

        // Act
        Lead savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertNotNull(savedEntity.getId(), "Lead id can not be null");
    }
}
