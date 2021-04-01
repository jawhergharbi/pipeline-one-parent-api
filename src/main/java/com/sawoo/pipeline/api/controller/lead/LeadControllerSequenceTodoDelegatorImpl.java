package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Component
@Qualifier("leadControllerSequence")
public class LeadControllerSequenceTodoDelegatorImpl implements LeadControllerSequenceTodoDelegator {

    private final LeadService service;

    @Autowired
    public LeadControllerSequenceTodoDelegatorImpl(LeadService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> evalTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.evalInteractions(leadId, sequenceId, assigneeId));
    }
}
