package com.sawoo.pipeline.api.service.company;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.repository.CompanyRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
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
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyServiceTest extends BaseServiceTest<CompanyDTO, Company, CompanyRepository, CompanyService, CompanyMockFactory> {

    @MockBean
    private CompanyRepository repository;

    @Autowired
    public CompanyServiceTest(CompanyMockFactory mockFactory, CompanyService service) {
        super(mockFactory, DataStoreConstants.COMPANY_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Company component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(CompanyDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Company entity) {
        doReturn(Optional.of(entity)).when(repository).findByName(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("findByName: entity found - Success")
    void findByNameWhenEntityExitsReturnsSuccess() {
        // Set up mock entities
        String COMPANY_ID = getMockFactory().getComponentId();
        String COMPANY_NAME = getMockFactory().getFAKER().company().name();
        Company mockedEntity = getMockFactory().newEntity(COMPANY_ID);
        mockedEntity.setName(COMPANY_NAME);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByName(COMPANY_NAME);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = getService().findByName(COMPANY_NAME);

        // Assert the response
        Assertions.assertTrue(returnedEntity.isPresent(), String.format("Company entity with name [%s] was not found", COMPANY_NAME));
        Assertions.assertEquals(returnedEntity.get().getName(), COMPANY_NAME, "Company.name should be the same");

        verify(repository, times(1)).findByName(COMPANY_NAME);
    }

    @Test
    @DisplayName("findByName: entity not found return optional empty - Failure")
    void findByNameWhenCompanyDoesNotExitsReturnsOptionalEmpty() {
        // Set up mock entities
        String COMPANY_NAME = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByName(COMPANY_NAME);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = getService().findByName(COMPANY_NAME);

        // Assert the response
        Assertions.assertFalse(returnedEntity.isPresent(), String.format("Company entity with name [%s] was found", COMPANY_NAME));

        verify(repository, Mockito.times(1)).findByName(COMPANY_NAME);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String COMPANY_ID = getMockFactory().getComponentId();
        String COMPANY_NAME = getMockFactory().getFAKER().company().name();
        String COMPANY_URL = getMockFactory().getFAKER().company().url();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(COMPANY_NAME);
        mockedDTO.setUrl(COMPANY_URL);
        Company mockedEntity = getMockFactory().newEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByName(anyString());
        doReturn(mockedEntity).when(repository).insert(any(Company.class));

        // Execute the service call
        CompanyDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Company can not be null");
        Assertions.assertEquals(COMPANY_NAME, returnedEntity.getName(), "Company.name should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(repository, times(1)).insert(any(Company.class));
        verify(repository, times(1)).findByName(anyString());
    }

    @Test
    @DisplayName("update: entity name when company does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPANY_ID = getMockFactory().getComponentId();
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(NEW_COMPANY_NAME);

        Company mockedEntity = getMockFactory().newEntity(COMPANY_ID);
        mockedEntity.setName(NEW_COMPANY_NAME);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        CompanyDTO returnedDTO = getService().update(COMPANY_ID, mockedDTO);

        Assertions.assertNotNull(returnedDTO, "Company entity is not null");
        Assertions.assertEquals(
                NEW_COMPANY_NAME,
                returnedDTO.getName(),
                String.format("Name must be '%s'", NEW_COMPANY_NAME));

        verify(repository, times(1)).findById(COMPANY_ID);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("update: when entity does not exist - Failure")
    void updateWhenCompanyNotFoundReturnsResourceNotFoundExceptionFailure() {
        // Set up mocked entities
        String COMPANY_ID = getMockFactory().getComponentId();
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(NEW_COMPANY_NAME);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute and assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().update(COMPANY_ID, mockedDTO),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }
}
