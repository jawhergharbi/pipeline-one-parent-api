package com.sawoo.pipeline.api.service.base;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.mock.MockFactory;
import com.sawoo.pipeline.api.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@NoArgsConstructor
@Getter
public abstract class BaseServiceTest<D, M extends BaseEntity, R extends MongoRepository<M, String>, S extends BaseService<D>, F extends MockFactory<D, M>> {

    @Setter
    private R repository;
    private F mockFactory;
    private String entityType;
    private S service;

    public BaseServiceTest(F mockFactory, String entityType, S service) {
        this.mockFactory = mockFactory;
        this.entityType = entityType;
        this.service = service;
    }

    protected abstract String getEntityId(M component);
    protected abstract String getDTOId(D component);
    protected abstract void mockedEntityExists(M entity);

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityDoesExitReturnsSuccess() {
        // Set up mock entities
        String COMPONENT_ID = mockFactory.getComponentId();
        M mockedEntity = mockFactory.newEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        D returnedEntity = service.findById(COMPONENT_ID);

        // Assert the response
        Assertions.assertNotNull(
                returnedEntity,
                String.format("Entity type [%s] with id [%s] was not found", entityType, COMPONENT_ID));
        Assertions.assertEquals(
                COMPONENT_ID,
                getDTOId(returnedEntity),
                String.format("[%s].id should be equals to [%s]", entityType, COMPONENT_ID));

        verify(repository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("findById: entity does not exists - Failure")
    void findByIdWhenEntityNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        String COMPONENT_ID = mockFactory.getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(COMPONENT_ID),
                "findById must throw a ResourceNotFoundException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("create: when entity does exist - Failure")
    void createWhenEntityExistsReturnsCommonException() {
        // Set up mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D mockedDTO = getMockFactory().newDTO(COMPONENT_ID);
        M mockedEntity = getMockFactory().newEntity(COMPONENT_ID);

        // Set up the mocked repository
        mockedEntityExists(mockedEntity);

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
    }

    @Test
    @DisplayName("findAll: multiple entities found - Success")
    void findAllWhenThereAreMultipleEntitiesReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<M> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(entityList).when(repository).findAll();

        // Execute the service call
        List<D> returnedList = service.findAll();

        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedList.size(), "Returned list size must be 2");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll: no entities found - Success")
    void findAllWhenThereAreNoEntitiesReturnsSuccess() {
        // Set up the mocked repository
        doReturn(Collections.emptyList()).when(repository).findAll();

        // Execute the service call
        List<D> returnedList = service.findAll();

        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("delete: entity found - Success")
    void deleteWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        M mockedEntity = getMockFactory().newEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        D returnedDTO = service.delete(COMPONENT_ID);

        Assertions.assertNotNull(
                returnedDTO,
                String.format("Entity type [%s] with id [%s] can not be null", entityType, COMPONENT_ID));
        Assertions.assertEquals(
                COMPONENT_ID,
                getDTOId(returnedDTO), String.format("[%s].id fields are the same", entityType));

        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("delete: entity not found - failure")
    void deleteWhenEntityNotFoundReturnsResourceNotFoundException() {
        // Set up mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute and assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(COMPONENT_ID),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("update: when entity does not exist - Failure")
    void updateWhenEntityNotFoundReturnsResourceNotFoundExceptionFailure() {
        // Set up mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D mockedDTO = getMockFactory().newDTO(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute and assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(COMPONENT_ID, mockedDTO),
                "update must throw an ResourceNotFoundException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("update: when entity does not exist - Failure")
    void updateWhenIdIsNullOrEmptyReturnsConstrainViolationException() {
        // Set up mocked entities
        D mockedDTO = getMockFactory().newDTO(null);

        // Execute and assert
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.update(null, mockedDTO),
                "update must throw an ConstraintViolationException");

        Assertions.assertFalse(
                exception.getConstraintViolations().isEmpty(),
                String.format("There are constrains violation update entity type [%s]", entityType));
        Assertions.assertEquals(
                1,
                exception.getConstraintViolations().size(),
                String.format("Constrain Violations size [%d]", 1));

        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any());
    }
}
