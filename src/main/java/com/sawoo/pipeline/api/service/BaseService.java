package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface BaseService<D> {

    public D create(@Valid D dto) throws CommonServiceException;

    D findById(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id)
            throws ResourceNotFoundException;

    List<D> findAll();

    D delete(String id) throws ResourceNotFoundException;
}
