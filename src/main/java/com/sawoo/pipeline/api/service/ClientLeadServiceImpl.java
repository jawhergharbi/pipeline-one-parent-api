package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.prospect.Lead;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientLeadServiceImpl implements ClientLeadService {

    private final ClientRepositoryWrapper clientRepository;
    private final LeadServiceUtils leadServiceUtils;
    private final CommonServiceMapper mapper;

    @Override
    public LeadDTO create(Long clientId, LeadDTO lead, int type) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new prospect for client id: [{}]. Prospect: [{}]", clientId, lead);

        Client client = findClientById(clientId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        leadServiceUtils.preProcessLead(lead, now, type);

        client.getLeads().add(mapper.getLeadDTOToDomainMapper().getDestination(lead));
        client.setUpdated(now);

        Client entity = clientRepository.save(client);

        return mapper
                .getLeadDomainToDTOMapper()
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

                    return mapper.getLeadDomainToDTOMapper().getDestination(lead);
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

                    return mapper.getLeadDomainToDTOMapper().getDestination(lead);
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
                .map((lead) -> mapper.getLeadDomainToDTOMapper().getDestination(lead))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadMainDTO> findAllLeadsMain(LocalDateTime datetime) {
        log.debug("Retrieving leads for all the clients");

        List<Client> clients = clientRepository.findAll();

        List<LeadMainDTO> leadsList = getLeadsFromClients(clients, null, null, datetime);
        log.debug("[{}] lead/s found", leadsList.size());

        return leadsList;
    }

    @Override
    public List<LeadMainDTO> findLeadsMain(List<Long> clientIds, Integer statusMin, Integer statusMax, LocalDateTime datetime) {
        log.debug("Retrieve leads for client ids [{}]. Datetime: [{}]", clientIds, datetime);
        List<Client> clientList = clientRepository.findAllById(clientIds);
        if (clientList.size() < clientIds.size()) {
            log.warn(
                    "[{}] clients found for the following clientIds [{}]. Number of leads found does not match the clients requested",
                    clientList.size(),
                    clientIds);
        }
        return getLeadsFromClients(clientList, statusMin, statusMax, datetime);
    }

    private Client findClientById(Long id) throws ResourceNotFoundException {
        return clientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Client", String.valueOf(id)}));
    }

    private List<LeadMainDTO> getLeadsFromClients(List<Client> clients, Integer statusMin, Integer statusMax, LocalDateTime datetime) {
        List<Predicate<Lead>> predicates = new ArrayList<>();
        if (statusMin != null) {
            Predicate<Lead> filter = l -> l.getStatus().getValue() >= statusMin;
            predicates.add(filter);
        }
        if (statusMax != null) {
            Predicate<Lead> filter = l -> l.getStatus().getValue() <= statusMax;
            predicates.add(filter);
        }
        List<LeadMainDTO> leadsList = clients
                .stream()
                .flatMap((client) -> {
                    ClientBaseDTO clientBase = mapper.getClientDomainToDTOBaseMapper().getDestination(client);
                    if (predicates.size() > 0) {
                        return client.getLeads().stream()
                                .filter(predicates.stream().reduce(x->true, Predicate::and))
                                .map((lead) -> mapLead(lead, clientBase));
                    } else {
                        return client.getLeads().stream()
                                .map((lead) -> mapLead(lead, clientBase));
                    }
                }).collect(Collectors.toList());


        log.debug("[{}] lead found", leadsList.size());
        return leadsList;
    }

    private LeadMainDTO mapLead(Lead lead, ClientBaseDTO client) {
        LeadMainDTO leadDTO = mapper.getLeadDomainToDTOMainMapper().getDestination(lead);
        leadDTO.setClient(client);
        return leadDTO;
    }
}
