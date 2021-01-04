package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.interaction.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final InteractionService service;
    private final LeadRepository repository;

    @Override
    public InteractionDTO addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid InteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new interaction for lead id: [{}].", leadId);

        interaction.setComponentId(leadId);
        final InteractionDTO savedInteraction = service.create(interaction);

        log.debug("Lead interaction has been created for lead id: [{}]. Interaction id [{}]", leadId, interaction.getId());

        Lead lead = findLeadById(leadId);
        List<Interaction> interactions = lead.getInteractions();
        interactions.stream()
                .filter(i -> i.getScheduled().equals(savedInteraction.getScheduled()))
                .findAny()
                .ifPresent( (i) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                            new String[]{leadId, interaction.getScheduled().toString()});
                });

        interactions.add(service.getMapper().getMapperIn().getDestination(savedInteraction));
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(lead);

        return savedInteraction;
    }

    @Override
    public InteractionDTO deleteInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        return null;
    }


    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return repository
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }
}
