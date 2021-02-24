package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.repository.sequence.SequenceStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@Component
public class SequenceStepServiceDecorator implements SequenceStepService {

    private final SequenceStepRepository sequenceStepRepository;
    private final SequenceService sequenceService;

    @Autowired
    public SequenceStepServiceDecorator(@Lazy SequenceService sequenceService, SequenceStepRepository sequenceStepRepository) {
        this.sequenceService = sequenceService;
        this.sequenceStepRepository = sequenceStepRepository;
    }

    @Override
    public SequenceStepDTO addStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @Valid SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }

    @Override
    public SequenceStepDTO updateStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }

    @Override
    public SequenceStepDTO removeStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceStepId)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }

    @Override
    public List<SequenceStepDTO> getSteps(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId)
            throws ResourceNotFoundException {
        return null;
    }
}
