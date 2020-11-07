package com.sawoo.pipeline.api.service;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.repository.CompanyRepositoryMongo;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class CompanyServiceTest extends BaseServiceTest {

    @Autowired
    private CompanyService service;

    @MockBean
    private CompanyRepositoryMongo repository;

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenCompanyDoesExitReturnsSuccess() {
        // Set up mock entities
        String COMPANY_ID = FAKER.internet().uuid();
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        CompanyDTO returnedEntity = service.findById(COMPANY_ID);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, String.format("Company entity with id [%s] was not found", COMPANY_ID));
        Assertions.assertEquals(returnedEntity.getId(), COMPANY_ID, String.format("Company.id should be equals to [%s]", COMPANY_ID));

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
    }

    @Test
    @DisplayName("findById: company does not exists - Failure")
    void findByIdWhenCompanyNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        String COMPANY_ID = FAKER.internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(COMPANY_ID),
                "findById must throw a ResourceNotFoundException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, Mockito.times(1)).findById(anyString());
    }

    @Test
    @DisplayName("findByName: company found - Success")
    void findByNameWhenCompanyExitsReturnsSuccess() {
        // Set up mock entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);
        mockedEntity.setName(COMPANY_NAME);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByName(COMPANY_NAME);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = service.findByName(COMPANY_NAME);

        // Assert the response
        Assertions.assertTrue(returnedEntity.isPresent(), String.format("Company entity with name [%s] was not found", COMPANY_NAME));
        Assertions.assertEquals(returnedEntity.get().getName(), COMPANY_NAME, "Company.name should be the same");

        verify(repository, Mockito.times(1)).findByName(COMPANY_NAME);
    }

    @Test
    @DisplayName("findByName: company not found return optional empty - Failure")
    void findByNameWhenCompanyDoesNotExitsReturnsOptionalEmpty() {
        // Set up mock entities
        String COMPANY_NAME = FAKER.company().name();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByName(COMPANY_NAME);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = service.findByName(COMPANY_NAME);

        // Assert the response
        Assertions.assertFalse(returnedEntity.isPresent(), String.format("Company entity with name [%s] was found", COMPANY_NAME));

        verify(repository, Mockito.times(1)).findByName(COMPANY_NAME);
    }


    @Test
    @DisplayName("create: when company does not exist - Success")
    void createWhenCompanyDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(COMPANY_NAME);
        mockedDTO.setUrl(COMPANY_URL);
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL, LocalDateTime.now());

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByName(anyString());
        doReturn(mockedEntity).when(repository).insert(any(CompanyMongoDB.class));

        // Execute the service call
        CompanyDTO returnedEntity = service.create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Company can not be null");
        Assertions.assertEquals(COMPANY_NAME, returnedEntity.getName(), "Company.name should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(repository, Mockito.times(1)).insert(any(CompanyMongoDB.class));
        verify(repository, Mockito.times(1)).findByName(anyString());
    }

    @Test
    @DisplayName("create: when company does exist - Failure")
    void createWhenCompanyExistsReturnsCommonException() {
        // Set up mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(COMPANY_NAME);
        mockedDTO.setUrl(COMPANY_URL);
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByName(COMPANY_NAME);

        // Asserts
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.create(mockedDTO),
                "create must throw a CommonServiceException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION);
        Assertions.assertEquals(
                2,
                exception.getArgs().length,
                "Number of arguments in the exception must be 2");

        verify(repository, Mockito.times(1)).findByName(COMPANY_NAME);
    }

    @Test
    @DisplayName("findAll: multiple entities found - Success")
    void findAllWhenThereAreMultipleCompaniesReturnsSuccess() {
        // Set up mock entities
        int listSize = 2;
        List<CompanyMongoDB> companyList = IntStream.range(0, listSize)
                .mapToObj((company) -> {
                    String COMPANY_ID = FAKER.internet().uuid();
                    return getMockFactory().newCompanyEntity(COMPANY_ID);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(companyList).when(repository).findAll();

        // Execute the service call
        List<CompanyDTO> returnedList = service.findAll();

        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(2, returnedList.size(), "Returned list size must be 2");

        verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("findAll: empty list - Success")
    void findAllWhenThereAreNoCompanyEntitiesReturnsSuccess() {
        // Set up the mocked repository
        doReturn(Collections.emptyList()).when(repository).findAll();

        // Execute the service call
        List<CompanyDTO> returnedList = service.findAll();

        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");

        verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("delete: company entity found - Success")
    void deleteWhenCompanyEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String COMPANY_NAME = Faker.instance().company().name();
        String COMPANY_URL = Faker.instance().company().url();
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        CompanyDTO returnedDTO = service.delete(COMPANY_ID);

        Assertions.assertNotNull(returnedDTO, "Returned entity can not be null");
        Assertions.assertEquals(COMPANY_ID, returnedDTO.getId(), "company.id fields are the same");
        Assertions.assertEquals(COMPANY_NAME, returnedDTO.getName(), "company.name fields are the same");

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
        verify(repository, Mockito.times(1)).delete(any());
    }

    @Test
    @DisplayName("delete: company entity not found - failure")
    void deleteWhenCompanyEntityNotFoundReturnsResourceNotFoundException() {
        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute and assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(anyString()),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: company name when company does exist - Success")
    void updateWhenCompanyFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(NEW_COMPANY_NAME);
        mockedDTO.setId(COMPANY_ID);

        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyMongoDB mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        CompanyDTO returnedDTO = service.update(mockedDTO);

        Assertions.assertNotNull(returnedDTO, "Company entity is not null");
        Assertions.assertEquals(
                NEW_COMPANY_NAME,
                returnedDTO.getName(),
                String.format("Name must be '%s'", NEW_COMPANY_NAME));

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
        verify(repository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("update: when company does not exist - Failure")
    void updateWhenCompanyNotFoundReturnsResourceNotFoundExceptionFailure() {
        // Set up mocked entities
        String COMPANY_ID = FAKER.internet().uuid();
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(NEW_COMPANY_NAME);
        mockedDTO.setId(COMPANY_ID);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute and assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(mockedDTO),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(COMPANY_ID);
    }
}
