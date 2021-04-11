package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Qualifier("prospectControllerTODO")
public class ProspectControllerTodoDelegatorImpl implements ProspectControllerTodoDelegator {

    private final ProspectService service;

    @Autowired
    public ProspectControllerTodoDelegatorImpl(ProspectService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<TodoDTO> addTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @Valid TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException {
        TodoDTO newEntity = service.addTODO(prospectId, todo);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI
                            + "/"
                            + prospectId
                            + "/"
                            + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME
                            + "/"
                            + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<TodoDTO> removeTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeTODO(prospectId, todoId));
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getTODOs(prospectId));
    }

    @Override
    public ResponseEntity<TodoAssigneeDTO> getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getTODO(prospectId, todoId));
    }

    @Override
    public ResponseEntity<List<ProspectTodoDTO>> searchTODOs(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearch searchCriteria) {
        return ResponseEntity.ok().body(service.searchBy(searchCriteria));
    }
}
