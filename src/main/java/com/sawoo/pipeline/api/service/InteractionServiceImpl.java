package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadBaseDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<InteractionDTO> getByType(Integer[] types, Long[] clients) {
        log.debug("Retrieve interactions for client ids [{}] and interaction types [{}]", clients, types);
        List<Client> clientList = clientRepository.findAllById(Arrays.asList(clients));
        if (clientList.size() < clients.length) {
            log.warn(
                    "[{}] clients found for the following clientIds [{}]. Number of leads found does not match the clients requested",
                    clientList.size(),
                    clients);
        }

        return clientList.stream().flatMap( (client) -> {
            ClientBaseDTO clientDTO = mapper.getClientDomainToDTOBaseMapper().getDestination(client);
            return client.getLeads().stream().flatMap( (lead) -> {
                LeadBaseDTO leadDTO = mapper.getLeadDomainToDTOBaseMapper().getDestination(lead);
                List<LeadInteraction> interactions = lead.getInteractions();
                if (types != null && types.length > 0) {
                    List<Integer> typeListToFilter = Arrays.asList(types);
                    Predicate<LeadInteraction> typeFilter = i -> typeListToFilter.contains(i.getType());
                    return interactions.stream()
                            .filter(typeFilter)
                            .map((interaction) -> mapInteraction(interaction, clientDTO, leadDTO));
                } else {
                    return interactions.stream()
                            .map((interaction) -> mapInteraction(interaction, clientDTO, leadDTO));
                }
            });
        }).collect(Collectors.toList());
    }

    private InteractionDTO mapInteraction(LeadInteraction interaction, ClientBaseDTO client, LeadBaseDTO lead) {
        InteractionDTO interactionDTO = mapper.getInteractionDomainToDTOMapper().getDestination(interaction);
        interactionDTO.setClient(client);
        interactionDTO.setLead(lead);
        return  interactionDTO;
    }
}
