package com.sawoo.pipeline.api.repository.person;

import com.sawoo.pipeline.api.mock.PersonMockFactory;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class PersonRepositoryTest extends BaseRepositoryTest<Person, PersonRepository, PersonMockFactory> {

    private static final String PERSON_JSON_DATA_FILE_NAME = "person-test-data.json";
    private static final String PERSON_ID = "5fa3ce63rt4ef23d963da45b";
    private static final String PERSON_LINKED_IN_URL = "http://linkedin.com/miguel.miguelin";

    private final CompanyRepository companyRepository;

    @Autowired
    public PersonRepositoryTest(PersonRepository repository, PersonMockFactory mockFactory, CompanyRepository companyRepository) {
        super(repository, PERSON_JSON_DATA_FILE_NAME, PERSON_ID, Person.class.getSimpleName(), mockFactory);
        this.companyRepository = companyRepository;
    }

    @Override
    protected Class<Person[]> getClazz() {
        return Person[].class;
    }

    @Override
    protected String getComponentId(Person component) {
        return component.getId();
    }

    @Override
    protected Person getNewEntity() {
        String PERSON_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(PERSON_ID, false);
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
        List<Person> people = getRepository().findAll();

        Assertions.assertEquals(
                getDocumentSize(),
                people.size(),
                String.format("Should be %d Person entities in the database", getDocumentSize()));
        Assertions.assertNotNull(
                people.get(0).getCompany(),
                "Person's company can not be null");
        Assertions.assertNotNull(
                people.get(0).getCompany().getId(),
                "Person's company id can not be null");
    }

    @Test
    @DisplayName("findByLinkedInUrl: entities found - Success")
    void findByLinkedInUrlWhenEntityIdFoundReturnsSuccess() {
        Optional<Person> entity = getRepository().findByLinkedInUrl(PERSON_LINKED_IN_URL);

        Assertions.assertTrue(
                entity.isPresent(),
                String.format("Person with [linkedInUrl]: %s can not be null", PERSON_LINKED_IN_URL));
        Assertions.assertEquals(
                PERSON_LINKED_IN_URL,
                entity.get().getLinkedInUrl(),
                String.format("Person [linkedInUrl] must be %s", PERSON_LINKED_IN_URL));
    }

    @Test
    @DisplayName("insert: company cascade saving - Success")
    void insertWhenCompanyDoesNotExistReturnsSuccess() {
        Person entity = getMockFactory().newEntity(null);
        entity.getCompany().setId(null);

        Person savedEntity =  getRepository().insert(entity);

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
        Person entity = getMockFactory().newEntity(null);
        String COMPANY_URL_UPDATED = getMockFactory().getFAKER().company().url();
        company.setUrl(COMPANY_URL_UPDATED);
        entity.setCompany(company);

        // execute repository action
        Person savedEntity =  getRepository().insert(entity);
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
                () -> updatedCompany.ifPresent( c -> Assertions.assertEquals(
                        COMPANY_URL_UPDATED,
                        c.getUrl(),
                        String.format("Company url must be [%s]", COMPANY_URL_UPDATED))));
    }

    @Test
    @DisplayName("save: company cascade updating - Success")
    void saveWhenCompanyDoesExistAndCompanyIsUpdatedReturnsSuccess() {
        // Arrange
        Optional<Person> person = getRepository().findById(PERSON_ID);
        if (person.isEmpty()) {
            Assertions.fail(String.format("Person with id [%s] was not found", PERSON_ID));
        }
        int COMPANY_HEADCOUNT = getMockFactory().getFAKER().number().numberBetween(50, 500);
        Person entity = person.get();
        Company company = entity.getCompany();
        String COMPANY_ID = company.getId();
        company.setHeadcount(COMPANY_HEADCOUNT);

        // execute repository action
        Person savedEntity =  getRepository().save(entity);
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
                () -> updatedCompany.ifPresent( c -> Assertions.assertEquals(
                        COMPANY_HEADCOUNT,
                        c.getHeadcount(),
                        String.format("Company headcount must be [%s]", COMPANY_HEADCOUNT))));
    }
}
