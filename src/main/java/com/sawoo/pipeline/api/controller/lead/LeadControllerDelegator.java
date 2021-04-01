package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Component
@Primary
public class LeadControllerDelegator extends BaseControllerDelegator<LeadDTO, LeadService> implements
        LeadControllerReportDelegator, LeadControllerInteractionDelegator, LeadControllerSequenceInteractionDelegator, LeadControllerCustomDelegator {

    private final LeadControllerReportDelegator reportDelegator;
    private final LeadControllerInteractionDelegator leadInteractionDelegator;
    private final LeadControllerSequenceInteractionDelegator leadSequenceInteractionDelegator;

    @Autowired
    public LeadControllerDelegator(
            LeadService service,
            @Qualifier("leadControllerReport") LeadControllerReportDelegator reportDelegator,
            @Qualifier("leadControllerInteraction") LeadControllerInteractionDelegator leadInteractionDelegator,
            @Qualifier("leadControllerSequence") LeadControllerSequenceInteractionDelegator leadSequenceInteractionDelegator) {
        super(service, ControllerConstants.LEAD_CONTROLLER_API_BASE_URI);
        this.reportDelegator = reportDelegator;
        this.leadInteractionDelegator = leadInteractionDelegator;
        this.leadSequenceInteractionDelegator = leadSequenceInteractionDelegator;
    }

    @Override
    public String getComponentId(LeadDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<InputStreamResource> getReport(String id, String template, String lan) {
        return reportDelegator.getReport(id, template, lan);
    }

    @Override
    public ResponseEntity<TodoDTO> addInteraction(String leadId, TodoDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        return leadInteractionDelegator.addInteraction(leadId, interaction);
    }

    @Override
    public ResponseEntity<TodoDTO> removeInteraction(String leadId, String interactionId) {
        return leadInteractionDelegator.removeInteraction(leadId, interactionId);
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> getInteractions(String leadId) throws ResourceNotFoundException {
        return leadInteractionDelegator.getInteractions(leadId);
    }

    @Override
    public ResponseEntity<TodoAssigneeDTO> getInteraction(String leadId, String interactionId) throws ResourceNotFoundException {
        return leadInteractionDelegator.getInteraction(leadId, interactionId);
    }

    @Override
    public ResponseEntity<LeadDTO> deleteLeadSummary(String leadId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteLeadSummary(leadId));

    }

    @Override
    public ResponseEntity<LeadDTO> deleteLeadQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteLeadQualificationComments(leadId));
    }

    @Override
    public ResponseEntity<LeadDTO> deleteLeadCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteLeadCompanyComments(leadId));
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> evalInteractions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        return leadSequenceInteractionDelegator.evalInteractions(leadId, sequenceId, assigneeId);
    }
}
