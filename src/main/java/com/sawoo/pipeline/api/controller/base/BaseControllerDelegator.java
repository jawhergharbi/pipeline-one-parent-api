package com.sawoo.pipeline.api.controller.base;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.service.base.BaseService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Validated
public abstract class BaseControllerDelegator<D, S extends BaseService<D>> implements ControllerDelegation<D> {

    @Getter
    private final S service;
    private final String resourceURI;

    public BaseControllerDelegator(S service, String resourceURI) {
        this.service = service;
        this.resourceURI = resourceURI;
    }

    @Override
    public ResponseEntity<D> create(@Valid D dto) {
        D newEntity = service.create(dto);
        try {
            return ResponseEntity
                    .created(new URI(resourceURI + "/" + getComponentId(newEntity)))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<D>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @Override
    public ResponseEntity<D> findById(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @Override
    public ResponseEntity<D> deleteById(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id) {
        return ResponseEntity.ok().body(service.delete(id));
    }

    @Override
    public ResponseEntity<?> update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            D dto) {
        D entityUpdated = service.update(id, dto);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI(resourceURI + "/" + getComponentId(entityUpdated)))
                    .body(entityUpdated);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<VersionDTO<D>>> getVersions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id) {
        return ResponseEntity.ok().body(getService().getVersions(id));
    }
}
