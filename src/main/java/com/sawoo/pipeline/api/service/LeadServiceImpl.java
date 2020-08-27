package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.LeadRepository;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadServiceImpl implements LeadService {

    private final CommonServiceMapper mapper;
    private final LeadServiceUtils utils;
    private final LeadRepository repository;

    @Override
    public LeadBasicDTO create(LeadBasicDTO lead) throws CommonServiceException {
        log.debug("Creating new lead. Name: [{}]", lead.getFullName());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        utils.preProcessLead(lead, now);

        Lead entity = mapper.getLeadDTOToDomainMapper().getDestination(lead);
        entity = repository.save(entity);

        log.debug("Lead has been successfully created. Entity: {}", entity);

        return mapper.getLeadDomainToDTOBaseMapper().getDestination(entity);
    }

    @Override
    public LeadBasicDTO findById(Long id) throws ResourceNotFoundException {
        log.debug("Retrieving lead by id. Id: [{}]", id);

        return repository
                .findById(id)
                .map(mapper.getLeadDomainToDTOBaseMapper()::getDestination)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(id)}));
    }

    @Override
    public List<LeadBasicDTO> findAll() {
        log.debug("Retrieving all lead entities");
        List<LeadBasicDTO> leads = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(mapper.getLeadDomainToDTOBaseMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] lead/s has/have been found", leads.size());
        return leads;
    }

    @Override
    public List<LeadMainDTO> findAllMain(LocalDateTime datetime) {
        log.debug("Retrieve all lead entities together with their next and previous interactions. Date time: [{}]", datetime);

        List<LeadMainDTO> leads = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map((lead) -> {
                    LeadMainDTO leadDTO = mapper.getLeadDomainToDTOMainMapper().getDestination(lead);
                    List<LeadInteraction> interactions =
                            lead.getInteractions()
                                    .stream().sorted(Comparator.comparing(LeadInteraction::getScheduled))
                                    .collect(Collectors.toList());
                    log.debug("Lead id [{}] has [{}] interactions", leadDTO.getId(), interactions.size());

                    // find next interaction
                    LeadUtils.findNextInteraction(interactions, datetime)
                            .ifPresent((interaction) ->
                                    leadDTO.setNext(mapper.getLeadInteractionDomainToDTOMapper().getDestination(interaction)));
                    // find last interaction
                    LeadUtils.findLastInteraction(interactions, datetime)
                            .ifPresent((interaction) ->
                                    leadDTO.setLast(mapper.getLeadInteractionDomainToDTOMapper().getDestination(interaction)));
                    return leadDTO;
                })
                .collect(Collectors.toList());
        log.debug("[{}] leads has been found", leads.size());
        return leads;
    }

    @Override
    public Optional<LeadBasicDTO> delete(Long id) {
        log.debug("Deleting lead entity with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("Lead entity with id: [{}] has been deleted", id);
                    return Optional.of(mapper.getLeadDomainToDTOBaseMapper().getDestination(company));
                })
                .orElseGet(() -> {
                    log.info("Lead entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public Optional<LeadBasicDTO> update(Long id, LeadBasicDTO leadDTO) {
        log.debug("Updating lead with id: [{}]. Lead: [{}]", id, leadDTO);

        return repository
                .findById(id)
                .map((lead) -> {
                    lead = mapper
                            .getLeadDTOToDomainMapper()
                            .getDestination(
                                    lead,
                                    leadDTO,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(lead);

                    log.debug("Lead entity with id [{}] has been successfully updated. Updated data: [{}]", id, leadDTO);
                    return Optional.of(mapper.getLeadDomainToDTOBaseMapper().getDestination(lead));
                })
                .orElseGet(() -> {
                    log.info("Lead entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }
}
