package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI)
public class SequenceController {

    private final SequenceControllerDelegator delegator;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> create(@RequestBody SequenceDTO dto) {
        return delegator.create(dto);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceDTO>> getAll() {
        return delegator.findAll();
    }

   @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<SequenceDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @GetMapping(
            value = "/accounts/{accountIds}/main",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceDTO>> findByAccountIds(
            @PathVariable(value = "accountIds") Set<String> accountIds) {
        return delegator.findByAccounts(accountIds);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @DeleteMapping(
            value = "/{id}/user/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> deleteUser(
            @PathVariable String id,
            @PathVariable String userId) {
        return delegator.deleteUser(id, userId);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody SequenceDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @PostMapping(
            value = "/{id}/steps",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> addStep(
            @PathVariable("id") String sequenceId,
            @RequestBody SequenceStepDTO step) {
        return delegator.addStep(sequenceId, step);
    }

    @DeleteMapping(
            value = "/{id}/steps/{stepId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> removeStep(
            @PathVariable("id") String sequenceId,
            @PathVariable("stepId") String sequenceStepId) {
        return delegator.removeStep(sequenceId, sequenceStepId);
    }

    @PutMapping(
            value = "/{id}/steps/{stepId}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> updateStep(
            @PathVariable("id") String sequenceId,
            @PathVariable("stepId") String sequenceStepId,
            @RequestBody SequenceStepDTO step) {
        step.setId(sequenceStepId);
        return delegator.updateStep(sequenceId, step);
    }

    @GetMapping(
            value = "/{id}/steps",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceStepDTO>> getSteps(
            @PathVariable("id") String sequenceId) {
        return delegator.getSteps(sequenceId);
    }

    @GetMapping(
            value = "/{id}/steps/search-personality",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceStepDTO>> getStepsByPersonality(
            @PathVariable("id") String sequenceId,
            @RequestParam(value = "personality", required = false) Integer personality) {
        return delegator.getStepsByPersonality(sequenceId, personality);
    }
}
