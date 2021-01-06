package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
public interface LeadControllerInteractionDelegator {

    ResponseEntity<InteractionDTO> addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @Valid InteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<InteractionDTO> removeInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException;
}
