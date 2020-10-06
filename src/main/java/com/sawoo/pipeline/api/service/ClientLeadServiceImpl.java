package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.client.datastore.ClientRepository;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientLeadServiceImpl implements ClientLeadService {

    private final ClientRepository clientRepository;
    private final LeadServiceUtils leadServiceUtils;
    private final CommonServiceMapper mapper;

    @Override
    public LeadDTO create(Long clientId, LeadDTO lead) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new lead for client id: [{}]. Lead: [{}]", clientId, lead);

        Client client = findClientById(clientId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        leadServiceUtils.preProcessLead(lead, now);

        client.getLeads().add(mapper.getLeadDTOToDomainMapper().getDestination(lead));
        client.setUpdated(now);

        Client entity = clientRepository.save(client);

        return mapper
                .getLeadDomainToDTOBaseMapper()
                .getDestination(
                        entity
                                .getLeads()
                                .stream()
                                .filter((clientLead) -> clientLead.getLinkedInUrl().equals(lead.getLinkedInUrl()))
                                .findFirst()
                                .map((newLead) -> {
                                    log.debug(
                                            "Lead id [{}] has been created and added to client id [{}]. Lead: [{}]",
                                            newLead.getId(),
                                            clientId,
                                            newLead);
                                    return newLead;
                                })
                                .orElseThrow(() -> new CommonServiceException(
                                        ExceptionMessageConstants.COMMON_REFERENCE_CHILD_WAS_NOT_FOUND_ERROR,
                                        new String[]{"Lead", lead.getLinkedInUrl(), "Client", String.valueOf(clientId)}))
                );
    }

    @Override
    public LeadDTO add(Long clientId, Long leadId) throws ResourceNotFoundException {
        log.debug("Add lead id [{}] to client id[{}]", leadId, clientId);

        Client client = findClientById(clientId);

        return leadServiceUtils.findById(leadId)
                .map((lead) -> {
                    client.getLeads().add(lead);
                    client.setUpdated(LocalDateTime.now(ZoneOffset.UTC));

                    if (client.getStatus().getValue() == DomainConstants.ClientStatus.ON_BOARDING.ordinal()) {
                        client.getStatus().setValue(DomainConstants.ClientStatus.RUNNING.ordinal());
                    }

                    clientRepository.save(client);

                    log.debug(
                            "Lead id [{}] has been added to client id [{}]. Client lead size: [{}]. Added lead: [{}]",
                            leadId,
                            clientId,
                            client.getLeads().size(),
                            lead);

                    return mapper.getLeadDomainToDTOBaseMapper().getDestination(lead);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Lead", String.valueOf(leadId)}));
    }

    @Override
    public LeadDTO remove(Long clientId, Long leadId) throws ResourceNotFoundException {
        log.debug("Remove lead id [{}] to client id[{}]", leadId, clientId);

        Client client = findClientById(clientId);

        return leadServiceUtils.findById(leadId)
                .map((lead) -> {
                    client.getLeads().remove(lead);
                    client.setUpdated(LocalDateTime.now(ZoneOffset.UTC));

                    clientRepository.save(client);

                    log.debug(
                            "Lead id [{}] has been removed from client id [{}]. Client lead size: [{}]. Removed lead: [{}]",
                            leadId,
                            clientId,
                            client.getLeads().size(),
                            lead);

                    return mapper.getLeadDomainToDTOBaseMapper().getDestination(lead);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Lead", String.valueOf(leadId)}));
    }

    @Override
    public List<LeadDTO> findAll(Long clientId) throws ResourceNotFoundException {
        log.debug("Retrieving leads for client id [{}]", clientId);

        return findClientById(clientId)
                .getLeads()
                .stream()
                .map((lead) -> mapper.getLeadDomainToDTOBaseMapper().getDestination(lead))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadMainDTO> findAllMain(LocalDateTime datetime) {
        log.debug("Retrieving leads for all the clients");

        List<Client> clients = StreamSupport
                .stream(clientRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        List<LeadMainDTO> leadsList = getLeadsFromClients(clients, datetime);
        log.debug("[{}] lead found", leadsList.size());

        return leadsList;
    }

    @Override
    public List<LeadMainDTO> findClientsMain(List<Long> clientIds, LocalDateTime datetime) {
        log.debug("Retrieve leads for client ids [{}]. Datetime: [{}]", clientIds, datetime);
        List<Client> clientList = StreamSupport
                .stream(clientRepository.findAllById(clientIds).spliterator(), false)
                .collect(Collectors.toList());
        if (clientList.size() < clientIds.size()) {
            log.warn(
                    "[{}] clients found for the following clientIds [{}]. Number of leads found does not match the clients requested",
                    clientList.size(),
                    clientIds);
        }
        return getLeadsFromClients(clientList, datetime);
    }

    private Client findClientById(Long id) throws ResourceNotFoundException {
        return clientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Client", String.valueOf(id)}));
    }

    private List<LeadMainDTO> getLeadsFromClients(List<Client> clients, LocalDateTime datetime) {
        List<LeadMainDTO> leadsList = clients
                .stream()
                .flatMap((client) -> {
                    ClientBaseDTO clientBase = mapper.getClientDomainToDTOBaseMapper().getDestination(client);
                    return client.getLeads().stream().map((lead) -> {
                        LeadMainDTO leadMain = mapper.getLeadDomainToDTOMainMapper().getDestination(lead);
                        List<LeadInteraction> interactions =
                                lead.getInteractions()
                                        .stream().sorted(Comparator.comparing(LeadInteraction::getScheduled))
                                        .collect(Collectors.toList());
                        log.debug("Lead id [{}] has [{}] interactions", leadMain.getId(), interactions.size());

                        // find next interaction
                        LeadUtils.findNextInteraction(interactions, datetime)
                                .ifPresent((interaction) ->
                                        leadMain.setNext(mapper.getLeadInteractionDomainToDTOMapper().getDestination(interaction)));
                        // find last interaction
                        LeadUtils.findLastInteraction(interactions, datetime)
                                .ifPresent((interaction) ->
                                        leadMain.setLast(mapper.getLeadInteractionDomainToDTOMapper().getDestination(interaction)));
                        leadMain.setClient(clientBase);
                        return leadMain;
                    });
                })
                .collect(Collectors.toList());

        log.debug("[{}] lead found", leadsList.size());
        return leadsList;
    }
}
