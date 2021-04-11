package com.sawoo.pipeline.api.service.base;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeSaveEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeUpdateEvent;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Getter
public abstract class BaseServiceImpl<D, M extends BaseEntity, R extends BaseMongoRepository<M>, OM extends BaseMapper<D, M>> implements BaseService<D>, BaseProxyService<R, OM> {

    private OM mapper;
    private R repository;
    private String entityType;
    private ApplicationEventPublisher eventPublisher;
    private AuditService audit;

    protected BaseServiceImpl(R repository, OM mapper, String entityType, ApplicationEventPublisher eventPublisher, AuditService audit) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityType = entityType;
        this.eventPublisher = eventPublisher;
        this.audit = audit;
    }

    protected BaseServiceImpl(R repository,OM mapper, String entityType, AuditService audit) {
        this(repository, mapper, entityType, null, audit);
    }

    public abstract Optional<M> entityExists(D entityToCreate);

    @Override
    public D create(@Valid D dto) throws CommonServiceException {
        log.debug("Creating new entity type: [{}]", getEntityType());

        entityExists(dto)
                .ifPresent(entity -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{ getEntityType(), dto.toString()});
                });
        M entity = mapper.getMapperIn().getDestination(dto);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setCreated(now);
        entity.setUpdated(now);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new BaseServiceBeforeInsertEvent<>(dto, entity));
        }
        entity = repository.insert(entity);

        log.debug("Entity type [{}] has been successfully created. Entity: [{}]", getEntityType(), entity);

        return mapper.getMapperOut().getDestination(entity);
    }

    @Override
    public D findById(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id)
            throws ResourceNotFoundException {
        log.debug("Retrieve [{}] by id. Id: [{}]", entityType, id);

        return repository
                .findById(id)
                .map(entity -> mapper.getMapperOut().getDestination(entity))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ getEntityType(), id }));
    }

    @Override
    public List<D> findAll() {
        log.debug("Retrieve all entities. Entity: [{}]", entityType);
        List<D> entities = repository
                .findAll()
                .stream()
                .map(mapper.getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] [{}] entity/entities has/have been found", entities.size(), entityType);
        return entities;
    }

    @Override
    public D delete(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)  String id) throws ResourceNotFoundException {
        log.debug("Delete [{}] entity with id: [{}]", entityType, id);

        return repository
                .findById(id)
                .map(entity -> {
                    repository.deleteById(id);
                    log.debug("[{}] entity with id: [{}] has been deleted", entityType, id);
                    return mapper.getMapperOut().getDestination(entity);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ getEntityType(), id }));
    }

    @Override
    public D update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)  String id,
            D dto) throws ResourceNotFoundException {
        log.debug("Update entity type [{}] with id: [{}]", entityType, id);

        return getRepository()
                .findById(id)
                .map(entity -> {
                    if (eventPublisher != null) {
                        eventPublisher.publishEvent(new BaseServiceBeforeUpdateEvent<>(dto, entity));
                    }
                    entity = getMapper().getMapperIn()
                            .getDestination(
                                    entity,
                                    dto,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    entity.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    if (eventPublisher != null) {
                        eventPublisher.publishEvent(new BaseServiceBeforeSaveEvent<>(dto, entity));
                    }
                    getRepository().save(entity);

                    log.debug(
                            "Entity type [{}] with id [{}] has been successfully updated. Updated data: [{}]",
                            entityType,
                            id,
                            entity);
                    return getMapper().getMapperOut().getDestination(entity);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ getEntityType(), id }));
    }

    @Override
    public List<D> deleteByIds(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) List<String> ids) {
        log.debug("Delete [{}] entity with ids: [{}]", entityType, ids);

        List<D> entities = repository
                .deleteByIdIn(ids)
                .stream()
                .map(mapper.getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] entity/entities of type [{}] has/have deleted", entities.size(), entityType);
        return entities;
    }

    @Override
    public List<VersionDTO<D>> getVersions(String id) {
        return getRepository()
                .findById(id)
                .map( entity -> audit.getVersions(entity, id, getMapper().getMapperOut()))
                .orElse(null);
    }
}
