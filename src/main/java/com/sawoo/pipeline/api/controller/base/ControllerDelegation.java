package com.sawoo.pipeline.api.controller.base;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface ControllerDelegation<D> {

    ResponseEntity<D> create(@Valid D dto);

    ResponseEntity<List<D>> findAll();

    ResponseEntity<D> findById(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id);

    ResponseEntity<D> deleteById(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id);

    ResponseEntity<?> update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            D dto);

    String getComponentId(D dto);
}
