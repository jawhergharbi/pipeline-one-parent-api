package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.service.sequencestep.SequenceStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SequenceStepsServiceDecorator implements SequenceStepsService {

    private final SequenceStepService sequenceStepService;
    private final SequenceService sequenceService;

    @Autowired
    public SequenceStepsServiceDecorator(@Lazy SequenceService sequenceService, SequenceStepService sequenceStepService) {
        this.sequenceService = sequenceService;
        this.sequenceStepService = sequenceStepService;
    }

    @Override
    public SequenceStepDTO addStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @Valid SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add new step for sequence id: [{}].", sequenceId);
        Sequence sequence = findSequenceById(sequenceId);

        List<SequenceStep> steps = sequence.getSteps();
        // Validating that the step is not created already
        if (steps.stream()
                .anyMatch(s -> s.getPosition().equals(step.getPosition()) && s.getPersonality().equals(step.getPersonality()))) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.SEQUENCE_STEP_ADD_STEP_POSITION_AND_PERSONALITY_ALREADY_FILLED_EXCEPTION,
                    new Object[] {sequenceId, step.getPosition(), step.getPersonality()});
        }

        final SequenceStepDTO stepCreated = sequenceStepService.create(step);

        log.debug("Sequence step has been created for sequence id: [{}]. Step id [{}]", sequenceId, step.getId());

        steps.add(sequenceStepService.getMapper().getMapperIn().getDestination(stepCreated));
        sequence.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        sequenceService.getRepository().save(sequence);

        return stepCreated;
    }

    @Override
    public SequenceStepDTO updateStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Update step id: [{}] from sequence id: [{}].", step.getId(), sequenceId);

        Sequence sequence = findSequenceById(sequenceId);
        return sequence.getSteps()
                .stream()
                .filter(s -> s.getId().equals(step.getId()))
                .findAny()
                .map( s -> sequenceStepService.update(step.getId(), step))
                .orElseThrow(() ->
                        new CommonServiceException(
                                ExceptionMessageConstants.SEQUENCE_STEP_UPDATE_STEP_NOT_FOUND_IN_THE_SEQUENCE_EXCEPTION,
                                new String[]{ sequenceId, step.getId() }));
    }

    @Override
    public SequenceStepDTO removeStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceStepId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Remove step id: [{}] from sequence id: [{}].", sequenceStepId, sequenceId);

        Sequence sequence = findSequenceById(sequenceId);
        return sequence.getSteps()
                .stream()
                .filter(s -> s.getId().equals(sequenceStepId))
                .findAny()
                .map( s -> {
                    sequence.getSteps().remove(s);
                    sequence.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    sequenceService.getRepository().save(sequence);

                    log.debug("Sequence step with id [{}] for sequence id [{}] has been deleted.", sequenceStepId, sequenceId);
                    return sequenceStepService.delete(sequenceStepId);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.SEQUENCE_STEP_DOCUMENT, sequenceStepId }));
    }

    @Override
    public List<SequenceStepDTO> getSteps(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId)
            throws ResourceNotFoundException {
        log.debug("Get sequence steps for sequence id: [{}].", sequenceId);
        Sequence sequence = findSequenceById(sequenceId);

        return sequence.getSteps()
                .stream()
                .map(sequenceStepService.getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
    }

    private Sequence findSequenceById(String sequenceId) throws ResourceNotFoundException {
        return sequenceService.getRepository()
                .findById(sequenceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.SEQUENCE_DOCUMENT, sequenceId }));
    }
}
