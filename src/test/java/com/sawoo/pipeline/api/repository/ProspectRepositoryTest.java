package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class ProspectRepositoryTest extends BaseRepositoryTest<Prospect, ProspectRepository> {

    private static final File LEAD_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "prospect-test-data.json").toFile();
    private static final String PROSPECT_ID = "5fa3ce63rt4ef23d963da45b";

    private final CompanyRepository companyRepository;

    @Autowired
    public ProspectRepositoryTest(ProspectRepository repository, CompanyRepository companyRepository) {
        super(repository, LEAD_JSON_DATA, PROSPECT_ID, Prospect.class.getSimpleName());
        this.companyRepository = companyRepository;
    }

    @Override
    protected Class<Prospect[]> getClazz() {
        return Prospect[].class;
    }

    @Override
    protected String getComponentId(Prospect component) {
        return component.getId();
    }

    @Override
    protected Prospect getNewEntity() {
        String PROSPECT_ID = FAKER.internet().uuid();
        return getMockFactory().newProspectEntity(PROSPECT_ID, false);
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        getRepository().deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("findAll: return all the entities defined in the test file and check company has been stored - Success")
    void findAllAndReferenceEntityStoresReturnsSuccess() {
        List<Prospect> prospects = getRepository().findAll();

        Assertions.assertEquals(
                getDocumentSize(),
                prospects.size(),
                String.format("Should be %d Prospect entities in the database", getDocumentSize()));
        Assertions.assertNotNull(
                prospects.get(0).getCompany(),
                "Prospect's company can not be null");
        Assertions.assertNotNull(
                prospects.get(0).getCompany().getId(),
                "Prospect's company id can not be null");
    }

    @Test
    void findByLinkedInUrlWhenEntityIdFoundReturnsSuccess() {
        String PROSPECT_LINKED_IN_URL = "http://linkedin.com/miguel.miguelin";
        Optional<Prospect> entity = getRepository().findByLinkedInUrl(PROSPECT_LINKED_IN_URL);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("Prospect with [linkedInUrl]: %s can not be null", PROSPECT_LINKED_IN_URL));
        Assertions.assertEquals(
                PROSPECT_LINKED_IN_URL,
                entity.get().getLinkedInUrl(),
                String.format("Prospect [linkedInUrl] must be %s", PROSPECT_LINKED_IN_URL));
    }
}
