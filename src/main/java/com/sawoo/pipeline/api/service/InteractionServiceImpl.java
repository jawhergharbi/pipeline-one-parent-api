package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadBaseDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteractionServiceImpl implements InteractionService {

    private final ClientRepositoryWrapper clientRepository;
    private final CommonServiceMapper mapper;

    @Override
    public List<InteractionDTO> findBy(Integer[] types, Integer[] status, Long[] clients) {
        log.debug("Retrieve interactions for client ids [{}] with type [{}] and status [{}]", clients, types, status);

        List<Client> clientList = (clients != null && clients.length > 0) ?
                clientRepository.findAllById(Arrays.asList(clients)) :
                clientRepository.findAll();

        if (clients != null && (clientList.size() < clients.length)) {
            log.warn(
                    "[{}] clients found for the following clientIds [{}]. Number of leads found does not match the clients requested",
                    clientList.size(),
                    clients);
        }

        return clientList.stream().flatMap( (client) -> {
            ClientBaseDTO clientDTO = mapper.getClientDomainToDTOBaseMapper().getDestination(client);
            return client.getLeads().stream().flatMap( (lead) -> {
                LeadBaseDTO leadDTO = mapper.getLeadDomainToDTOBaseMapper().getDestination(lead);
                List<LeadInteractionOld> interactions = lead.getInteractions();
                List<Predicate<LeadInteractionOld>> predicates = getFilters(types, status);
                if (predicates.size() > 0) {
                    return interactions.stream()
                            .filter(predicates.stream().reduce(x -> true, Predicate::and))
                            .map((interaction) -> mapInteraction(interaction, clientDTO, leadDTO));
                } else {
                    return interactions.stream()
                            .map((interaction) -> mapInteraction(interaction, clientDTO, leadDTO));
                }
            });
        }).collect(Collectors.toList());
    }

    private List<Predicate<LeadInteractionOld>> getFilters(Integer[] types, Integer[] statusList) {
        List<Predicate<LeadInteractionOld>> filters = new ArrayList<>();
        if (types != null && types.length > 0) {
            Predicate<LeadInteractionOld> typeFilter = i -> Arrays.asList(types).contains(i.getType());
            filters.add(typeFilter);
        }
        if (statusList != null && statusList.length > 0) {
            Predicate<LeadInteractionOld> statusFilter = i -> Arrays.asList(statusList).contains(i.getStatus());
            filters.add(statusFilter);
        }
        return filters;
    }

    private InteractionDTO mapInteraction(LeadInteractionOld interaction, ClientBaseDTO client, LeadBaseDTO lead) {
        InteractionDTO interactionDTO = mapper.getInteractionDomainToDTOMapper().getDestination(interaction);
        interactionDTO.setClient(client);
        interactionDTO.setLead(lead);
        return  interactionDTO;
    }
}
