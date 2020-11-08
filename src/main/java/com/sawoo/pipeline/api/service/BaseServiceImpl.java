package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.model.EntityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Getter
public abstract class BaseServiceImpl<D, M extends EntityBase, R extends MongoRepository<M, String>> implements BaseService<D> {

    private BaseMapper<D, M> mapper;
    private R repository;
    private String entityType;

    @Override
    public D create(@Valid D dto) throws CommonServiceException {
        log.debug("Creating new entity type: [{}]", entityType);

        entityExists(dto)
                .ifPresent((company) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{ entityType, dto.toString()});
                });
        M entity = mapper.getMapperIn().getDestination(dto);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setCreated(now);
        entity.setUpdated(now);
        entity = repository.insert(entity);

        log.debug("Entity type [{}] has been successfully created. Entity: [{}]", entityType, entity);

        return mapper.getMapperOut().getDestination(entity);
    }

    public BaseServiceImpl(R repository, BaseMapper<D, M> mapper, String entityType) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityType = entityType;
    }

    public abstract Optional<M> entityExists(D entityToCreate);

    @Override
    public D findById(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id)
            throws ResourceNotFoundException {
        log.debug("Retrieving [{}] by id. Id: [{}]", entityType, id);

        return repository
                .findById(id)
                .map((entity) -> mapper.getMapperOut().getDestination(entity))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{entityType, id}));
    }

    @Override
    public List<D> findAll() {
        log.debug("Retrieving all entities. Entity: [{}]", entityType);
        List<D> entities = repository
                .findAll()
                .stream()
                .map(mapper.getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] [{}] entity/entities has/have been found", entities.size(), entityType);
        return entities;
    }

    @Override
    public D delete(String id) throws ResourceNotFoundException {
        log.debug("Deleting [{}] entity with id: [{}]", entityType, id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("[{}] entity with id: [{}] has been deleted", entityType, id);
                    return mapper.getMapperOut().getDestination(company);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{entityType, id}));
    }
}
