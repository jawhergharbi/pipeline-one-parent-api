package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Qualifier("leadControllerInteraction")
public class LeadControllerInteractionDelegatorImpl implements LeadControllerInteractionDelegator {

    private final LeadService service;

    @Autowired
    public LeadControllerInteractionDelegatorImpl(LeadService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<InteractionDTO> addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid InteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        InteractionDTO newEntity = service.addInteraction(leadId, interaction);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.LEAD_CONTROLLER_API_BASE_URI + "/" + leadId + "/interactions/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<InteractionDTO> removeInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeInteraction(leadId, interactionId));
    }

    @Override
    public ResponseEntity<List<InteractionDTO>> getInteractions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getInteractions(leadId));
    }

    @Override
    public ResponseEntity<InteractionDTO> getInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getInteraction(leadId, interactionId));
    }
}
