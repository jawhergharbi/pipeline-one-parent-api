package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import com.sawoo.pipeline.api.repository.person.PersonRepository;
import org.junit.jupiter.api.AfterEach;
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

import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadRepositoryTest extends BaseRepositoryTest<Lead, LeadRepository, LeadMockFactory> {

    private static final String LEAD_JSON_DATA_FILE_NAME = "lead-test-data.json";
    private static final String LEAD_ID = "601c1e3de7681e0dbe0aa540";

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
    @DisplayName("save: lead cascade saving - Success")
    void findByIdWhenLeadExistsAndHaveTodosReturnsSuccess() {
        Optional<Lead> lead =  getRepository().findById(LEAD_ID);
        // There is no cascading saving with todos in the lead entity
        int TODO_SIZE = 0;

        Assertions.assertAll(
                String.format(
                        "Lead with id [%s] must be found and must have [%d] todos",
                        LEAD_ID,
                        TODO_SIZE),
                () -> Assertions.assertTrue(lead.isPresent(), "Lead can not be null"),
                () -> lead.ifPresent(l -> Assertions.assertTrue(
                        l.getTodos().isEmpty(),
                        "Todo list can not be null")));
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
