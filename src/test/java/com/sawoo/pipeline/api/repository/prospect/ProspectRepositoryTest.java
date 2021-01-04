package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
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
public class ProspectRepositoryTest extends BaseRepositoryTest<Prospect, ProspectRepository, ProspectMockFactory> {

    private static final File PROSPECT_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "prospect-test-data.json").toFile();
    private static final String PROSPECT_ID = "5fa3ce63rt4ef23d963da45b";
    private static final String PROSPECT_LINKED_IN_URL = "http://linkedin.com/miguel.miguelin";

    private final CompanyRepository companyRepository;

    @Autowired
    public ProspectRepositoryTest(ProspectRepository repository, ProspectMockFactory mockFactory, CompanyRepository companyRepository) {
        super(repository, PROSPECT_JSON_DATA, PROSPECT_ID, Prospect.class.getSimpleName(), mockFactory);
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
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(PROSPECT_ID, false);
    }

    @AfterEach
    protected void afterEach() {
        // Drop the entity collection so we can start fresh
        super.afterEach();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("findAll: return all the entities defined in the test file and check company has been stored - Success")
    void findAllWhenReferenceIsAlsoStoredReturnsSuccess() {
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
    @DisplayName("findByLinkedInUrl: entities found - Success")
    void findByLinkedInUrlWhenEntityIdFoundReturnsSuccess() {
        Optional<Prospect> entity = getRepository().findByLinkedInUrl(PROSPECT_LINKED_IN_URL);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("Prospect with [linkedInUrl]: %s can not be null", PROSPECT_LINKED_IN_URL));
        Assertions.assertEquals(
                PROSPECT_LINKED_IN_URL,
                entity.get().getLinkedInUrl(),
                String.format("Prospect [linkedInUrl] must be %s", PROSPECT_LINKED_IN_URL));
    }

    @Test
    @DisplayName("insert: company cascade saving - Success")
    void insertWhenCompanyDoesNotExistReturnsSuccess() {
        Prospect entity = getMockFactory().newEntity(null);
        entity.getCompany().setId(null);

        Prospect savedEntity =  getRepository().insert(entity);

        Assertions.assertAll("Company entity must be properly stored",
                () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
                () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"));
    }

    @Test
    @DisplayName("insert: company cascade updating - Success")
    void insertWhenCompanyDoesExistAndCompanyIsUpdatedReturnsSuccess() {
        // Arrange
        Company company = getMockFactory().getCompanyMockFactory().newEntity(null);
        company = companyRepository.insert(company);
        String COMPANY_ID = company.getId();
        Prospect entity = getMockFactory().newEntity(null);
        String COMPANY_URL_UPDATED = getMockFactory().getFAKER().company().url();
        company.setUrl(COMPANY_URL_UPDATED);
        entity.setCompany(company);

        // execute repository action
        Prospect savedEntity =  getRepository().insert(entity);
        Optional<Company> updatedCompany = companyRepository.findById(COMPANY_ID);


        // assertions
        Assertions.assertAll("Company entity must be properly updated",
                () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
                () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"),
                () -> Assertions.assertEquals(
                        COMPANY_URL_UPDATED,
                        savedEntity.getCompany().getUrl(),
                        String.format("Company url must be [%s]", COMPANY_URL_UPDATED)),
                () -> Assertions.assertTrue(updatedCompany.isPresent(), "Company updated entity can not be null"),
                () -> Assertions.assertEquals(
                        COMPANY_URL_UPDATED,
                        updatedCompany.get().getUrl(),
                        String.format("Company url must be [%s]", COMPANY_URL_UPDATED)));
    }

    @Test
    @DisplayName("save: company cascade updating - Success")
    void saveWhenCompanyDoesExistAndCompanyIsUpdatedReturnsSuccess() {
        // Arrange
        Optional<Prospect> prospect = getRepository().findById(PROSPECT_ID);
        if (prospect.isEmpty()) {
            Assertions.fail(String.format("Prospect with id [%s] was not found", PROSPECT_ID));
        }
        int COMPANY_HEADCOUNT = getMockFactory().getFAKER().number().numberBetween(50, 500);
        Prospect entity = prospect.get();
        Company company = entity.getCompany();
        String COMPANY_ID = company.getId();
        company.setHeadcount(COMPANY_HEADCOUNT);

        // execute repository action
        Prospect savedEntity =  getRepository().save(entity);
        Optional<Company> updatedCompany = companyRepository.findById(COMPANY_ID);


        // assertions
        Assertions.assertAll("Company entity must be properly updated",
                () -> Assertions.assertNotNull(savedEntity.getCompany(), "Company entity can not be null"),
                () -> Assertions.assertNotNull(savedEntity.getCompany().getId(), "Company id can not be null"),
                () -> Assertions.assertEquals(
                        COMPANY_HEADCOUNT,
                        savedEntity.getCompany().getHeadcount(),
                        String.format("Company headcount must be [%d]", COMPANY_HEADCOUNT)),
                () -> Assertions.assertTrue(updatedCompany.isPresent(), "Company updated entity can not be null"),
                () -> Assertions.assertEquals(
                        COMPANY_HEADCOUNT,
                        updatedCompany.get().getHeadcount(),
                        String.format("Company headcount must be [%s]", COMPANY_HEADCOUNT)));
    }
}
