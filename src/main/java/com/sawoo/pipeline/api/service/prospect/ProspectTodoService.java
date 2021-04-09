package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface ProspectTodoService {

    TodoDTO addTODO(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
                    @Valid TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException;

    <T extends TodoDTO> List<TodoDTO> addTODOList(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
                    @Valid List<T> todoList)
            throws ResourceNotFoundException, CommonServiceException;

    TodoDTO removeTODO(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
                       @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException;

    List<TodoAssigneeDTO> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException;

    TodoAssigneeDTO getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException;

    List<ProspectTodoDTO> findBy(List<String> prospectIds, List<Integer> status, List<Integer> types);
}