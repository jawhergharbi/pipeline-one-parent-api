package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import com.sawoo.pipeline.api.repository.person.PersonRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadRepositoryTest extends BaseRepositoryTest<Lead, LeadRepository, LeadMockFactory> {

    private static final String LEAD_JSON_DATA_FILE_NAME = "lead-test-data.json";
    private static final String LEAD_ID = "5fa3c963da6ra335fa2s323d45b";

    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;


    @Autowired
    public LeadRepositoryTest(
            LeadRepository repository,
            LeadMockFactory mockFactory,
            PersonRepository personRepository,
            CompanyRepository companyRepository) {
        super(repository, LEAD_JSON_DATA_FILE_NAME, LEAD_ID, Lead.class.getSimpleName(), mockFactory);
        this.personRepository = personRepository;
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
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("save: lead cascade saving - Success")
    void insertWhenPersonDoesNotExistReturnsSuccess() {
        Lead entity = getMockFactory().newEntity(null);

        Lead savedEntity =  getRepository().insert(entity);

        Assertions.assertAll("Person reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Lead id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson(),
                        "Person entity can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getId(),
                        "Person id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getCompany(),
                        "Person company can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getCompany().getId(),
                        "Person company id can not be null"));
    }

    @Test
    @DisplayName("insert: lead save when person exists - Success")
    void insertWhenPersonDoesExistReturnsSuccess() {
        // Arrange
        Lead entity = getMockFactory().newEntity(null);
        Person personEntity = getMockFactory().getPersonMockFactory().newEntity(null);
        personRepository.insert(personEntity);
        entity.setPerson(personEntity);

        // Act
        Lead savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertAll("Person reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Lead id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson(),
                        "Person entity can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getId(),
                        "Person id can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getCompany(),
                        "Person company can not be null"),
                () -> Assertions.assertNotNull(
                        savedEntity.getPerson().getCompany().getId(),
                        "Person company id can not be null"));
    }

    @Test
    @DisplayName("insert: person reference null - Success")
    void insertWhenPersonReferenceNullReturnsSuccess() {
        // Arrange
        Lead entity = getMockFactory().newEntity(null);
        entity.setPerson(null);

        // Act
        Lead savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertNotNull(savedEntity.getId(), "Lead id can not be null");
    }
}
