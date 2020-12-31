package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import com.sawoo.pipeline.api.service.lead.LeadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Qualifier("leadControllerInteraction")
public class LeadControllerInteractionDelegatorImpl implements LeadControllerInteractionDelegator {

    private final LeadService service;

    @Autowired
    public LeadControllerInteractionDelegatorImpl(LeadServiceImpl service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<LeadInteractionDTO> createInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid LeadInteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        LeadInteractionDTO newEntity = service.createInteraction(leadId, interaction);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.LEAD_CONTROLLER_API_BASE_URI + "/" + leadId + "/interactions/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
