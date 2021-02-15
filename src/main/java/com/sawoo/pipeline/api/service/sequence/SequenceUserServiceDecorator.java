package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceUserServiceDecorator implements SequenceUserService {

    @Override
    public SequenceDTO deleteUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }
}
