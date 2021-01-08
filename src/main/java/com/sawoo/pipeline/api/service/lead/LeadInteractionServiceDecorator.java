package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final InteractionService service;
    private final LeadRepository repository;
    private final LeadMapper mapper;

    @Override
    public InteractionDTO addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid InteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Adding new interaction for lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        List<Interaction> interactions = lead.getInteractions();
        interactions.stream()
                .filter(i -> i.getScheduled().equals(interaction.getScheduled()))
                .findAny()
                .ifPresent( (i) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                            new String[]{leadId, interaction.getScheduled().toString()});
                });

        interaction.setComponentId(leadId);
        final InteractionDTO savedInteraction = service.create(interaction);

        log.debug("Lead interaction has been created for lead id: [{}]. Interaction id [{}]", leadId, interaction.getId());

        interactions.add(service.getMapper().getMapperIn().getDestination(savedInteraction));
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(lead);

        return savedInteraction;
    }

    @Override
    public InteractionDTO removeInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        log.debug("Removing interaction from lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        return lead.getInteractions()
                .stream()
                .filter(i -> i.getId().equals(interactionId))
                .findAny()
                .map( i -> {
                    lead.getInteractions().remove(i);
                    lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(lead);
                    log.debug("Interaction with id [{}] for lead id [{}] has been deleted.", interactionId, leadId);
                    return service.delete(i.getId());
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.INTERACTION_DOCUMENT, interactionId }));
    }

    @Override
    public List<InteractionDTO> getInteractions(String leadId) throws ResourceNotFoundException {
        log.debug("Getting interactions from lead id: [{}].", leadId);
        List<InteractionDTO> interactions = findLeadById(leadId)
                .getInteractions()
                .stream()
                .map(service.getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] interactions has been found for lead id [{}]", leadId, interactions.size());
        return interactions;
    }

    @Override
    public InteractionDTO getInteraction(String leadId, String interactionId) throws ResourceNotFoundException {
        log.debug("Getting interaction id [{}] from lead id: [{}].", interactionId, leadId);
        Lead lead = findLeadById(leadId);
        return lead
                .getInteractions()
                .stream()
                .filter(i -> interactionId.equals(i.getId()))
                .findAny()
                .map(i -> {
                    log.debug("Interaction id [{}] for lead id [{}] has been found. \nInteraction: [{}]", interactionId, leadId, i);
                    return service.getMapper().getMapperOut().getDestination(i);
                })
                .orElseThrow( () ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, interactionId }) );
    }

    @Override
    public List<LeadInteractionDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        log.debug("Getting interactions from leads [{}] with status [{}] and types[{}]", leadIds, status, types);

        List<InteractionDTO> interactions = service.findBy(leadIds, status, types);
        if (interactions.size() > 0) {
            List<Lead> leads = leadIds.size() > 0 ? repository.findAllByIdIn(leadIds) : Collections.emptyList();

            // throw exception if leads.size < leadIds.size

            return interactions
                    .stream()
                    .map((i) -> {
                        LeadInteractionDTO interaction = mapper.getInteractionMapperOut().getDestination(i);
                        Optional<Lead> lead = leads.stream().filter(l -> l.getId().equals(interaction.getComponentId())).findAny();
                        lead.ifPresent(value -> interaction.setFullName(value.getPerson().getFullName()));
                        return interaction;
                    }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
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
