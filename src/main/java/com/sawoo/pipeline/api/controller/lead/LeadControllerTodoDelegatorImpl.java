package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Qualifier("leadControllerTODO")
public class LeadControllerTodoDelegatorImpl implements LeadControllerTodoDelegator {

    private final LeadService service;

    @Autowired
    public LeadControllerTodoDelegatorImpl(LeadService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<TodoDTO> addTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException {
        TodoDTO newEntity = service.addInteraction(leadId, todo);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.LEAD_CONTROLLER_API_BASE_URI + "/" + leadId + "/interactions/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<TodoDTO> removeTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeInteraction(leadId, todoId));
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getInteractions(leadId));
    }

    @Override
    public ResponseEntity<TodoAssigneeDTO> getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getInteraction(leadId, todoId));
    }
}
