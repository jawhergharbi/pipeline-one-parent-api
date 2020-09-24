package com.sawoo.pipeline.api.service;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.repository.CompanyRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CompanyServiceTest extends BaseServiceTest {

    @Autowired
    private CompanyService service;

    @MockBean
    private CompanyRepository repository;

    @Test
    @DisplayName("Company Service: findById - Success")
    void findByIdWhenCompanyDoesExitReturnsSuccess() {
        // Set up mock entities
        Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Company mockedEntity = newMockedEntity(COMPANY_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        CompanyDTO returnedEntity = service.findById(COMPANY_ID);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, String.format("Company entity with id [%d] was not found", COMPANY_ID));
        Assertions.assertEquals(returnedEntity.getId(), COMPANY_ID, "Company.id should be the same");

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
    }

    @Test
    @DisplayName("Company Service: findById when company does not exists - Failure")
    void findByIdWhenCompanyNotFoundReturnsResourceNot_FoundException() {
        // Set up mock entities
        Long COMPONENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(COMPONENT_ID);

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(COMPONENT_ID),
                "findById must throw a ResourceNotFoundException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, Mockito.times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("Company Service: findByName - Success")
    void findByNameWhenCompanyExitsReturnsSuccess() {
        // Set up mock entities
        Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        Company mockedEntity = newMockedEntity(COMPANY_ID);
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
    @DisplayName("Company Service: findByName - Optional Empty")
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
    @DisplayName("Company Service: create when company does not exist - Success")
    void createWhenCompanyDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(COMPANY_NAME);
        mockedDTO.setUrl(COMPANY_URL);
        Company mockedEntity = newMockedEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByName(COMPANY_NAME);
        doReturn(mockedEntity).when(repository).save(any());

        // Execute the service call
        CompanyDTO returnedEntity = service.create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Company entity with name " + COMPANY_NAME + " was found already in the system");
        Assertions.assertEquals(COMPANY_NAME, returnedEntity.getName(), "Company.name should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(repository, Mockito.times(1)).save(any());
        verify(repository, Mockito.times(1)).findByName(COMPANY_NAME);
    }

    @Test
    @DisplayName("Company service: create when company does exist - Failure")
    void createWhenCompanyExistsReturnsCommonException() {
        // Set up mocked entities
        Long COMPONENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(COMPANY_NAME);
        mockedDTO.setUrl(COMPANY_URL);
        Company mockedEntity = newMockedEntity(COMPONENT_ID, COMPANY_NAME, COMPANY_URL);

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
    @DisplayName("Company Service: findAll - Success")
    void findAllWhenThereAreTwoCompaniesReturnsSuccess() {
        // Set up mock entities
        Long COMPANY_ID_1 = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Long COMPANY_ID_2 = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        List<Company> companies = Arrays
                .asList(
                        newMockedEntity(COMPANY_ID_1),
                        newMockedEntity(COMPANY_ID_2));

        // Set up the mocked repository
        doReturn(companies).when(repository).findAll();

        // Execute the service call
        List<CompanyDTO> returnedList = service.findAll();

        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(2, returnedList.size(), "Returned list size must be 2");

        verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Company Service: findAll empty list - Success")
    void findAllWhenThereAreNoCompanyEntitiesReturnsSuccess() {
        // Set up mock entities
        List<Company> companies = Collections.emptyList();

        // Set up the mocked repository
        doReturn(companies).when(repository).findAll();

        // Execute the service call
        List<CompanyDTO> returnedList = service.findAll();

        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");

        verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Company Service: delete company entity found - Success")
    void deleteWhenCompanyEntityFoundReturnsSuccess() {
        // Set up mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String COMPANY_NAME = Faker.instance().company().name();
        String COMPANY_URL = Faker.instance().company().url();
        CompanyDTO mockedDTO = getMockFactory().newCompanyDTO(COMPANY_ID, COMPANY_NAME, COMPANY_URL);
        Company mockedEntity = newMockedEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        Optional<CompanyDTO> returnedDTO = service.delete(COMPANY_ID);

        Assertions.assertTrue(returnedDTO.isPresent(), "Returned entity can not be null");
        Assertions.assertEquals(mockedDTO.getId(), returnedDTO.get().getId(), "company.id fields are the same");
        Assertions.assertEquals(mockedDTO.getName(), returnedDTO.get().getName(), "company.name fields are the same");

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
        verify(repository, Mockito.times(1)).delete(any());
    }

    @Test
    @DisplayName("Company Service: delete company entity not found - Null")
    void deleteWhenCompanyEntityNotFoundReturnsNull() {
        // Set up mocked entities
        Long COMPONENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = service.delete(COMPONENT_ID);

        Assertions.assertFalse(returnedEntity.isPresent(), "Returned entity must be null");

        verify(repository, Mockito.times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("Company Service: update company name when company does exist - Success")
    void updateWhenCompanyFoundReturnsSuccess() {
        // Set up mocked entities
        CompanyDTO mockedDTO = new CompanyDTO();
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        mockedDTO.setName(NEW_COMPANY_NAME);

        Long COMPANY_ID = Faker.instance().number().randomNumber();
        String COMPANY_NAME = FAKER.company().name();
        String COMPANY_URL = FAKER.company().url();
        Company mockedEntity = getMockFactory().newCompanyEntity(COMPANY_ID, COMPANY_NAME, COMPANY_URL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(COMPANY_ID);

        // Execute the service call
        Optional<CompanyDTO> returnedDTO = service.update(COMPANY_ID, mockedDTO);

        Assertions.assertTrue(returnedDTO.isPresent(), "Company entity is not null");
        Assertions.assertEquals(
                NEW_COMPANY_NAME,
                returnedDTO.get().getName(),
                String.format("Name must be '%s'", NEW_COMPANY_NAME));

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
        verify(repository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("Company Service: update company when company does not exist - Failure")
    void updateWhenCompanyNotFoundReturnsFailure() {
        // Set up mocked entities
        Long COMPANY_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String NEW_COMPANY_NAME = Faker.instance().company().name();
        CompanyDTO mockedDTO = new CompanyDTO();
        mockedDTO.setName(NEW_COMPANY_NAME);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(COMPANY_ID);

        // Execute the service call
        Optional<CompanyDTO> returnedEntity = service.update(COMPANY_ID, mockedDTO);

        Assertions.assertFalse(returnedEntity.isPresent(), "Company entity must be null");

        verify(repository, Mockito.times(1)).findById(COMPANY_ID);
        verify(repository, Mockito.never()).save(any());
    }

    private Company newMockedEntity(Long id) {
        return newMockedEntity(
                id,
                Faker.instance().company().name(),
                Faker.instance().company().url());
    }

    private Company newMockedEntity(Long id, String name, String url) {
        Company entity = new Company();
        entity.setId(id);
        entity.setName(name);
        entity.setUrl(url);
        entity.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        entity.setCreated(LocalDateTime.now(ZoneOffset.UTC));
        return entity;
    }
}
