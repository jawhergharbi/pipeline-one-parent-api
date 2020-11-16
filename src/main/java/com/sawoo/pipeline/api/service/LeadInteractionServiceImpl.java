package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionRequestDTO;
import com.sawoo.pipeline.api.model.prospect.LeadInteraction;
import com.sawoo.pipeline.api.repository.LeadRepositoryOld;
import com.sawoo.pipeline.api.repository.interaction.LeadInteractionRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadInteractionServiceImpl implements LeadInteractionService {

    private final JMapper<LeadInteractionDTO, LeadInteraction> mapperDomainToDTO = new JMapper<>(LeadInteractionDTO.class, LeadInteraction.class);
    private final JMapper<LeadInteraction, LeadInteractionRequestDTO> mapperDTOToDomain = new JMapper<>(LeadInteraction.class, LeadInteractionRequestDTO.class);

    private final LeadRepositoryOld leadRepository;
    private final LeadInteractionRepositoryWrapper repository;

    @Override
    public LeadInteractionDTO create(Long leadId, LeadInteractionRequestDTO interaction) throws ResourceNotFoundException {
        log.debug("Creating new lead interaction. Lead id: [{}]. Interaction: [{}]", leadId, interaction);

        return leadRepository
                .findById(leadId)
                .map((lead) -> {
                    LeadInteraction entity = mapperDTOToDomain.getDestination(interaction, MappingType.ONLY_VALUED_FIELDS);
                    entity.setCreated(interaction.getDateTime());
                    entity.setUpdated(interaction.getDateTime());

                    entity = repository.save(leadId, entity);

                    log.debug("New lead interaction successfully created for Lead id: [{}]", leadId);

                    return mapperDomainToDTO.getDestination(entity);
                }).orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(leadId)})
                );
    }

    @Override
    public LeadInteractionDTO findById(Long leadId, Long id) throws ResourceNotFoundException {
        log.debug("Retrieve lead interaction by id. Lead id: [{}]. Interaction id: [{}]", leadId, id);

        return repository
                .findById(leadId, id)
                .map(mapperDomainToDTO::getDestination)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead Interaction",
                                        String.format(
                                                "Lead id: [%d]. Interaction id: [%d]",
                                                leadId,
                                                id)}
                        )
                );
    }

    @Override
    public List<LeadInteractionDTO> findAll(Long leadId) {
        log.debug("Retrieve lead interactions for lead id [{}]", leadId);

        return leadRepository
                .findById(leadId)
                .map((lead) -> {
                    List<LeadInteraction> interactions = lead.getInteractions();
                    log.debug(
                            "[{}] interactions has/have been found for lead id [{}]",
                            interactions.size(),
                            leadId);
                    return interactions
                            .stream()
                            .map(mapperDomainToDTO::getDestination).
                                    collect(Collectors.toList());
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<LeadInteractionDTO> delete(Long leadId, Long id) {
        log.debug("Delete lead interaction id[{}] for lead id [{}]", id, leadId);

        return repository
                .deleteById(leadId, id)
                .map(mapperDomainToDTO::getDestination);
    }

    @Override
    public Optional<LeadInteractionDTO> update(Long leadId, Long id, LeadInteractionRequestDTO interactionRequest) {
        log.debug("Update lead interaction id[{}] for lead id [{}]. Interaction: [{}]", id, leadId, interactionRequest);

        return repository
                .findById(leadId, id)
                .map((interaction) -> {
                    interaction = mapperDTOToDomain
                            .getDestination(
                                    interaction,
                                    interactionRequest,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    interaction.setUpdated(interactionRequest.getDateTime());
                    repository.save(interaction);

                    log.debug(
                            "Lead interaction entity with id [{}] from Lead id [{}] has been successfully updated. Updated data: [{}]",
                            id,
                            leadId,
                            interactionRequest);
                    return Optional.of(mapperDomainToDTO.getDestination(interaction));
                })
                .orElseGet(() -> {
                    log.info("Lead interaction entity with id [{}] for Lead id [{}] does not exist", id, leadId);
                    return Optional.empty();
                });
    }
}
