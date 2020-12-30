package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Component
@Qualifier("leadControllerInteraction")
public class LeadControllerInteractionDelegatorImpl implements LeadControllerInteractionDelegator {
    @Override
    public ResponseEntity<LeadInteractionDTO> createInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @Valid LeadInteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }
}
