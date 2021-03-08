package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI)
public class SequenceController {

    private final SequenceControllerDelegator delegator;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> create(@RequestBody SequenceDTO dto) {
        return delegator.create(dto);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceDTO>> getAll() {
        return delegator.findAll();
    }

   @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @RequestMapping(
            value = "/accounts/{accountIds}/main",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceDTO>> findByAccountIds(
            @PathVariable(value = "accountIds") Set<String> accountIds) {
        return delegator.findByAccounts(accountIds);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}/user/{userId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceDTO> deleteUser(
            @PathVariable String id,
            @PathVariable String userId) {
        return delegator.deleteUser(id, userId);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody SequenceDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @RequestMapping(
            value = "/{id}/steps",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> addStep(
            @PathVariable("id") String sequenceId,
            @RequestBody SequenceStepDTO step) {
        return delegator.addStep(sequenceId, step);
    }

    @RequestMapping(
            value = "/{id}/steps/{stepId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> removeStep(
            @PathVariable("id") String sequenceId,
            @PathVariable("stepId") String sequenceStepId) {
        return delegator.removeStep(sequenceId, sequenceStepId);
    }

    @RequestMapping(
            value = "/{id}/steps/{stepId}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SequenceStepDTO> updateStep(
            @PathVariable("id") String sequenceId,
            @PathVariable("stepId") String sequenceStepId,
            @RequestBody SequenceStepDTO step) {
        step.setId(sequenceStepId);
        return delegator.updateStep(sequenceId, step);
    }

    @RequestMapping(
            value = "/{id}/steps",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SequenceStepDTO>> getSteps(
            @PathVariable("id") String sequenceId) {
        return delegator.getSteps(sequenceId);
    }
}