package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI)
public class ProspectController {

    private final ProspectControllerDelegator delegator;

   @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ProspectDTO> create(
            @NotNull @RequestBody ProspectDTO prospect) {
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
            value = "/{id}/" + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME + "/search",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProspectTodoDTO>> searchTODOs(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR)
            @PathVariable("id") String id,
            @RequestParam(value = "status", required = false) List<Integer> status,
            @RequestParam(value = "types", required = false) List<Integer> types,
            @RequestParam(value = "sourceIds", required = false) List<String> sourceIds,
            @RequestParam(value = "sourceTypes", required = false) List<Integer> sourceTypes,
            @RequestParam(value = "accountIds", required = false) List<String> accountIds) {
        TodoSearch search = TodoSearch.builder()
                .componentIds(Collections.singletonList(id))
                .status(status)
                .types(types)
                .sourceId(sourceIds)
                .sourceType(sourceTypes)
                .accountIds(accountIds)
                .build();
        return delegator.searchTODOs(search);
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
