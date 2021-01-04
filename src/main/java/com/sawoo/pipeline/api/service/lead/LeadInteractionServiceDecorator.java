package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final InteractionRepository repository;
    private final LeadService leadService;

    public LeadInteractionServiceDecorator(InteractionRepository repository, LeadService leadService) {
        this.repository = repository;
        this.leadService = leadService;
    }

    @Override
    public LeadInteractionDTO createInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid LeadInteractionDTO interaction)
            throws CommonServiceException {
        log.debug("Creating new interaction for lead id: [{}].", leadId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Interaction leadInteraction = leadService.getMapper().getInteractionMapperIn().getDestination(interaction);
        leadInteraction.setLeadId(leadId);
        leadInteraction.setCreated(now);
        leadInteraction.setUpdated(now);
        repository.insert(leadInteraction);

        interaction = leadService.getMapper().getInteractionMapperOut().getDestination(leadInteraction);
        log.debug("Lead interaction has been created for lead id: [{}]. Interaction id [{}]", leadId, leadInteraction.getId());

        try {
            return leadService.addInteraction(leadId, interaction);
        } catch (ResourceNotFoundException exc) {
            repository.deleteById(interaction.getId());
            throw new CommonServiceException(
                    ExceptionMessageConstants.LEAD_INTERACTION_UPDATE_LEAD_WITH_INTERACTION_EXCEPTION,
                    new String[]{leadId, interaction.getId(), exc.getMessage()});
        }
    }

    @Override
    public LeadInteractionDTO deleteInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        return null;
    }
}
