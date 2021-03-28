package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Qualifier("sequenceStepController")
public class SequenceControllerStepDelegatorImpl implements SequenceControllerStepDelegator {

    private final SequenceService service;

    @Autowired
    public SequenceControllerStepDelegatorImpl(SequenceService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<SequenceStepDTO> addStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) @Valid SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        SequenceStepDTO newEntity = service.addStep(sequenceId, step);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI + "/" + sequenceId + "/steps/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<SequenceStepDTO> updateStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        SequenceStepDTO entityUpdated = service.updateStep(sequenceId, step);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI(ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI + "/" + sequenceId + "/steps/" + entityUpdated.getId()))
                    .body(entityUpdated);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<SequenceStepDTO> removeStep(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceStepId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeStep(sequenceId, sequenceStepId));
    }

    @Override
    public ResponseEntity<List<SequenceStepDTO>> getSteps(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getSteps(sequenceId));
    }

    @Override
    public ResponseEntity<List<SequenceStepDTO>> getStepsByPersonality(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            @Min(value = 1, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
            @Max(value = 4, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_EXCEED_MAX_SIZE_ERROR)
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) Integer personality)
            throws ResourceNotFoundException {
        List<SequenceStepDTO> steps = service.getStepsByPersonality(sequenceId, personality);
        return ResponseEntity.ok().body(steps);
    }
}
