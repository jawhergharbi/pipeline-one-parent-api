package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTypeRequestParam;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.ProspectStatusList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI)
public class ProspectController {

    private final ProspectControllerDelegator delegator;

   @PostMapping(
            value = { "", "/{type}"},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> save(
            @PathVariable(required = false) ProspectTypeRequestParam type,
            @NotNull @RequestBody ProspectDTO prospect) {
        if (type != null && type.equals(ProspectTypeRequestParam.LEAD)) {
            prospect.setStatus(Status
                    .builder()
                    .value(ProspectStatusList.HOT.getStatus())
                    .updated(LocalDateTime.now()).build());
        } else {
            prospect.setStatus(Status
                    .builder()
                    .value(ProspectStatusList.TARGETABLE.getStatus())
                    .updated(LocalDateTime.now())
                    .build());
        }
        return delegator.create(prospect);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProspectDTO>> getAll() {
        return delegator.findAll();
    }

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> findById(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<ProspectDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @DeleteMapping(
            value = "/{id}/summary",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> deleteProspectSummary(@PathVariable String id) {
        return delegator.deleteProspectSummary(id);
    }

    @DeleteMapping(
            value = "/{id}/company-summary",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> deleteProspectCompanyComments(@PathVariable String id) {
        return delegator.deleteProspectCompanyComments(id);
    }

    @DeleteMapping(
            value = "/{id}/qualification-notes",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> deleteProspectQualificationNotes(@PathVariable String id) {
        return delegator.deleteProspectQualificationComments(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody ProspectDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @GetMapping(
            value = "/{id}/report",
            produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<InputStreamResource> getReport(
            @PathVariable("id") String id,
            @RequestParam(value = "template", required = false) String template,
            @RequestParam(value = "lan", required = false) String lan) {
        return delegator.getReport(id, template, lan);
    }

    @PostMapping(
            value = "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TodoDTO> addTODO(
            @PathVariable("id") String id,
            @NotNull @RequestBody TodoDTO todo) {
        return delegator.addTODO(id, todo);
    }

    @DeleteMapping(
            value = "/{id}/"  + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TodoDTO> removeTODO(
            @PathVariable("id") String id,
            @PathVariable("todoId") String todoId) throws ResourceNotFoundException {
        return delegator.removeTODO(id, todoId);
    }

    @GetMapping(
            value = "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TodoAssigneeDTO>> getTODOs(
            @PathVariable("id") String id) {
        return delegator.getTODOs(id);
    }

    @GetMapping(
            value = "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/{todoId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TodoAssigneeDTO> getTODO(
            @PathVariable("id") String id,
            @PathVariable("todoId") String todoId) {
        return delegator.getTODO(id, todoId);
    }

    @GetMapping(
            value = "/{id}/" + ControllerConstants.SEQUENCE_CONTROLLER_RESOURCE_NAME + "/{sequenceId}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/eval",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TodoAssigneeDTO>> evalTODOs(
            @PathVariable("id") String id,
            @PathVariable("sequenceId") String sequenceId,
            @RequestParam(value = "assigneeId", required = false) String assigneeId) {
        return delegator.evalTODOs(id, sequenceId, assigneeId);
    }
}
