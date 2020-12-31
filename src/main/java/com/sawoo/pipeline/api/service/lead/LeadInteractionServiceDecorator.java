package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.leadinteraction.LeadInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@Qualifier(value = "leadInteractionService")
@RequiredArgsConstructor
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final LeadInteractionRepository repository;
    private final LeadService leadService;

    @Override
    public LeadInteractionDTO createInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid LeadInteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new interaction for lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadInteraction leadInteraction = leadService.getMapper().getInteractionMapperIn().getDestination(interaction);
        leadInteraction.setLeadId(leadId);
        leadInteraction.setCreated(now);
        leadInteraction.setUpdated(now);
        repository.insert(leadInteraction);

        log.debug("Lead interaction has been created for lead id: [{}]. Interaction id [{}]", leadId, leadInteraction.getId());

        lead.getInteractions().add(leadInteraction);
        lead.setUpdated(now);
        leadService.getRepository().save(lead);

        return leadService.getMapper().getInteractionMapperOut().getDestination(leadInteraction);
    }

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return leadService.getRepository()
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }
}
