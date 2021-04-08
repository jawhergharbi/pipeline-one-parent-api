package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
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
public class ProspectControllerDelegator extends BaseControllerDelegator<ProspectDTO, ProspectService> implements
        ProspectControllerReportDelegator, ProspectControllerTodoDelegator, ProspectControllerSequenceTodoDelegator, ProspectControllerCustomDelegator {

    private final ProspectControllerReportDelegator reportDelegator;
    private final ProspectControllerTodoDelegator prospectTODODelegator;
    private final ProspectControllerSequenceTodoDelegator prospectSequenceTODODelegator;

    @Autowired
    public ProspectControllerDelegator(
            ProspectService service,
            @Qualifier("prospectControllerReport") ProspectControllerReportDelegator reportDelegator,
            @Qualifier("prospectControllerTODO") ProspectControllerTodoDelegator prospectTODODelegator,
            @Qualifier("prospectControllerSequence") ProspectControllerSequenceTodoDelegator prospectSequenceTODODelegator) {
        super(service, ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI);
        this.reportDelegator = reportDelegator;
        this.prospectTODODelegator = prospectTODODelegator;
        this.prospectSequenceTODODelegator = prospectSequenceTODODelegator;
    }

    @Override
    public String getComponentId(ProspectDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<InputStreamResource> getReport(String id, String template, String lan) {
        return reportDelegator.getReport(id, template, lan);
    }

    @Override
    public ResponseEntity<TodoDTO> addTODO(String prospectId, TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectTODODelegator.addTODO(prospectId, todo);
    }

    @Override
    public ResponseEntity<TodoDTO> removeTODO(String prospectId, String todoId) {
        return prospectTODODelegator.removeTODO(prospectId, todoId);
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> getTODOs(String prospectId) throws ResourceNotFoundException {
        return prospectTODODelegator.getTODOs(prospectId);
    }

    @Override
    public ResponseEntity<TodoAssigneeDTO> getTODO(String prospectId, String todoId) throws ResourceNotFoundException {
        return prospectTODODelegator.getTODO(prospectId, todoId);
    }

    @Override
    public ResponseEntity<ProspectDTO> deleteProspectSummary(String prospectId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteProspectSummary(prospectId));

    }

    @Override
    public ResponseEntity<ProspectDTO> deleteProspectQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteProspectQualificationComments(prospectId));
    }

    @Override
    public ResponseEntity<ProspectDTO> deleteProspectCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteProspectCompanyComments(prospectId));
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> evalTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectSequenceTODODelegator.evalTODOs(prospectId, sequenceId, assigneeId);
    }
}
