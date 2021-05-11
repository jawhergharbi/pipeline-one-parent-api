package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.model.prospect.Prospect;
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
class ProspectRepositoryTest extends BaseRepositoryTest<Prospect, ProspectRepository, ProspectMockFactory> {

    private static final String TEST_JSON_DATA_FILE_NAME = "prospect-test-data.json";
    private static final String ENTITY_ID = "601c1e3de7681e0dbe0aa540";

    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;


    @Autowired
    public ProspectRepositoryTest(
            ProspectRepository repository,
            ProspectMockFactory mockFactory,
            PersonRepository personRepository,
            CompanyRepository companyRepository) {
        super(repository, TEST_JSON_DATA_FILE_NAME, ENTITY_ID, Prospect.class.getSimpleName(), mockFactory);
        this.personRepository = personRepository;
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
        return getMockFactory().newEntity(PROSPECT_ID);
    }

    @AfterEach
    protected void afterEach() {
        super.afterEach();
        personRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("save: prospect cascade saving - Success")
    void insertWhenPersonDoesNotExistReturnsSuccess() {
        Prospect entity = getMockFactory().newEntity(null);

        Prospect savedEntity =  getRepository().insert(entity);

        Assertions.assertAll("Person reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Prospect id can not be null"),
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
    @DisplayName("save: prospect cascade saving - Success")
    void findByIdWhenProspectExistsAndHaveTodosReturnsSuccess() {
        Optional<Prospect> prospect =  getRepository().findById(ENTITY_ID);
        // There is no cascading saving with todos in the prospect entity
        int TODO_SIZE = 0;

        Assertions.assertAll(
                String.format(
                        "Prospect with id [%s] must be found and must have [%d] todos",
                        ENTITY_ID,
                        TODO_SIZE),
                () -> Assertions.assertTrue(prospect.isPresent(), "Prospect can not be null"),
                () -> prospect.ifPresent(l -> Assertions.assertTrue(
                        l.getTodos().isEmpty(),
                        "Todo list can not be null")));
    }

    @Test
    @DisplayName("insert: prospect save when person exists - Success")
    void insertWhenPersonDoesExistReturnsSuccess() {
        // Arrange
        Prospect entity = getMockFactory().newEntity(null);
        Person personEntity = getMockFactory().getPersonMockFactory().newEntity(null);
        personRepository.insert(personEntity);
        entity.setPerson(personEntity);

        // Act
        Prospect savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertAll("Person reference entity must be properly stored",
                () -> Assertions.assertNotNull(
                        savedEntity.getId(),
                        "Prospect id can not be null"),
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
        Prospect entity = getMockFactory().newEntity(null);
        entity.setPerson(null);

        // Act
        Prospect savedEntity =  getRepository().insert(entity);

        // Assert
        Assertions.assertNotNull(savedEntity.getId(), "Prospect id can not be null");
    }
}
