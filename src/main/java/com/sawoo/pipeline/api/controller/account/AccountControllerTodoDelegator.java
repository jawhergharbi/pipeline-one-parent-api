package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
public interface AccountControllerTodoDelegator {

    ResponseEntity<List<ProspectTodoDTO>> findAllTODOsIn(
            @NotEmpty (message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) List<String> accountIds,
            List<Integer> status,
            List<Integer> types)
            throws CommonServiceException;

    ResponseEntity<List<ProspectTodoDTO>> findAllTODOs(
            @NotEmpty (message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String accountId,
            List<Integer> status,
            List<Integer> types)
            throws CommonServiceException;
}
