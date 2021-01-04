package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public interface LeadInteractionService {

    LeadInteractionDTO createInteraction(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
                                         @Valid LeadInteractionDTO interaction) throws CommonServiceException;

    LeadInteractionDTO deleteInteraction(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
                                         @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId) throws ResourceNotFoundException;
}
