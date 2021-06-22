package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface ProspectControllerTodoDelegator {

    ResponseEntity<List<TodoDTO>> addTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) List<TodoDTO> todos)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<List<TodoDTO>> removeTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) List<String> todoIds)
            throws ResourceNotFoundException;

    ResponseEntity<List<TodoAssigneeDTO>> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException;

    ResponseEntity<TodoAssigneeDTO> getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException;

    ResponseEntity<List<ProspectTodoDTO>> searchTODOs(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearch searchCriteria);
}
