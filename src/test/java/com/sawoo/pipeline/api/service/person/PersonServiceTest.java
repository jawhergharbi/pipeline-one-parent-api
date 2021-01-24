package com.sawoo.pipeline.api.service.person;

import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.PersonMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.person.PersonRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonServiceTest extends BaseServiceTest<PersonDTO, Person, PersonRepository, PersonService, PersonMockFactory> {

    @MockBean
    private PersonRepository repository;

    @Autowired
    public PersonServiceTest(PersonMockFactory mockFactory, PersonService service) {
        super(mockFactory, DBConstants.PERSON_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Person component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(PersonDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Person entity) {
        doReturn(Optional.of(entity)).when(repository).findByLinkedInUrl(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String PERSON_ID = getMockFactory().getComponentId();
        String PERSON_LINKED_IN_URL = getMockFactory().getFAKER().internet().url();
        PersonDTO mockedDTO = getMockFactory().newDTO(PERSON_ID);
        mockedDTO.setLinkedInUrl(PERSON_LINKED_IN_URL);
        Person mockedEntity = getMockFactory().newEntity(PERSON_ID);
        mockedEntity.setLinkedInUrl(PERSON_LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(anyString());
        doReturn(mockedEntity).when(repository).insert(any(Person.class));

        // Execute the service call
        PersonDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Entity can not be null");
        Assertions.assertEquals(PERSON_LINKED_IN_URL, returnedEntity.getLinkedInUrl(), "Person.linkedInUrl should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(repository, Mockito.times(1)).findByLinkedInUrl(anyString());
        verify(repository, Mockito.times(1)).insert(any(Person.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PERSON_ID = getMockFactory().getComponentId();
        PersonDTO mockedDTO = new PersonDTO();
        String PERSON_NEW_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        mockedDTO.setLastName(PERSON_NEW_LAST_NAME);
        mockedDTO.setId(PERSON_ID);
        Person mockedEntity = getMockFactory().newEntity(PERSON_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(PERSON_ID);

        // Execute the service call
        PersonDTO returnedDTO = getService().update(PERSON_ID, mockedDTO);

        Assertions.assertNotNull(returnedDTO, "Person entity can not be null");
        Assertions.assertEquals(
                PERSON_NEW_LAST_NAME,
                returnedDTO.getLastName(),
                String.format("LastName must be '%s'", PERSON_NEW_LAST_NAME));

        verify(repository, Mockito.times(1)).findById(anyString());
        verify(repository, Mockito.times(1)).save(any());
    }
}
