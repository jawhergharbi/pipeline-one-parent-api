package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.leadinteraction.LeadInteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final LeadInteractionRepository repository;
    private final LeadService leadService;

    public LeadInteractionServiceDecorator(LeadInteractionRepository repository, LeadService leadService) {
        this.repository = repository;
        this.leadService = leadService;
    }

    @Override
    public LeadInteractionDTO createInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid LeadInteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new interaction for lead id: [{}].", leadId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadInteraction leadInteraction = leadService.getMapper().getInteractionMapperIn().getDestination(interaction);
        leadInteraction.setLeadId(leadId);
        leadInteraction.setCreated(now);
        leadInteraction.setUpdated(now);
        repository.insert(leadInteraction);

        interaction = leadService.getMapper().getInteractionMapperOut().getDestination(leadInteraction);
        log.debug("Lead interaction has been created for lead id: [{}]. Interaction id [{}]", leadId, leadInteraction.getId());

        try {
            leadService.updateInteraction(leadId, interaction);
            return interaction;
        } catch (ResourceNotFoundException exc) {
            repository.deleteById(interaction.getId());
            throw new CommonServiceException(
                    ExceptionMessageConstants.LEAD_INTERACTION_UPDATE_LEAD_WITH_INTERACTION_EXCEPTION,
                    new String[]{leadId, interaction.getId(), exc.getMessage()});
        }
    }

    /*private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return leadService
                .getRepository()
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }*/
}
