package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public interface SequenceStepService {

    SequenceStepDTO addStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @Valid SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException;

    SequenceStepDTO updateStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException;

    SequenceStepDTO removeStep(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId,
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceStepId)
            throws ResourceNotFoundException, CommonServiceException;

    List<SequenceStepDTO> getSteps(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String sequenceId)
            throws ResourceNotFoundException;
}
