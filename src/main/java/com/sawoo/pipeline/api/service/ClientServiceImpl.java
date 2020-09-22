package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.StatusDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.client.ClientLeadInteractionDTO;
import com.sawoo.pipeline.api.dto.client.ClientMainDTO;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepositoryWrapper repository;
    private final CommonServiceMapper mapper;
    private final ClientServiceUtils utils;
    private final CompanyService companyService;
    private final UserService userService;

    @Override
    public ClientBasicDTO create(ClientBasicDTO client) throws CommonServiceException {
        log.debug("Creating new client. Name: [{}]", client.getFullName());

        repository
                .findByLinkedInUrl(client.getLinkedInUrl())
                .ifPresent((clientItem) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{"Client", clientItem.getLinkedInUrl()});
                });
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Process company info
        processCompanyData(client, now);

        // Process client status
        if (client.getStatus() == null) {
            client.setStatus(StatusDTO
                    .builder()
                    .value(DomainConstants.ClientStatus.ON_BOARDING.ordinal())
                    .updated(now)
                    .build());
        }

        Client entity = mapper.getClientDTOToDomainMapper().getDestination(client);
        entity.setCreated(now);
        entity.setUpdated(now);
        entity = repository.save(entity);

        log.debug("Client has been successfully created. Entity: [{}]", entity);

        return mapper.getClientDomainToDTOBasicMapper().getDestination(entity);
    }

    @Override
    public Optional<ClientBasicDTO> findById(Long id) throws ResourceNotFoundException {
        log.debug("Retrieving client by id. Id: [{}]", id);

        return repository
                .findById(id)
                .map(mapper.getClientDomainToDTOBasicMapper()::getDestination)
                .or(() -> {
                    log.debug("Client id [{}] not found", id);
                    return Optional.empty();
                });
    }

    @Override
    public List<ClientBasicDTO> findAll() {
        log.debug("Retrieving all client entities");
        List<ClientBasicDTO> leads = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(mapper.getClientDomainToDTOBasicMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] client/s has/have been found", leads.size());
        return leads;
    }

    @Override
    public List<ClientMainDTO> findAllMain(LocalDateTime datetime) {
        log.debug("Retrieve all client entities together with their next interaction. Date time: [{}]", datetime);

        List<ClientMainDTO> clients = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map((client) -> {
                    ClientMainDTO clientDTO = mapper.getClientDomainToDTOMainMapper().getDestination(client);
                    utils.getNextInteraction(client.getLeads(), datetime)
                            .ifPresent((lead) -> {
                                log.debug(
                                        "Client [{}] id [{}] and lead full name [{}] and id [{}] has a next interaction",
                                        client.getFullName(),
                                        client.getId(),
                                        lead.getFullName(),
                                        lead.getId());

                                clientDTO.setNextInteraction(
                                        ClientLeadInteractionDTO
                                                .builder()
                                                .leadName(lead.getFullName())
                                                .interaction(
                                                        LeadUtils.findNextInteraction(lead.getInteractions(), datetime)
                                                                .map((interaction) ->
                                                                        mapper.getLeadInteractionDomainToDTOMapper().getDestination(interaction))
                                                                .orElse(null))
                                                .build());
                            });
                    return clientDTO;
                })
                .collect(Collectors.toList());
        log.debug("[{}] clients has been found", clients.size());
        return clients;
    }

    @Override
    public Optional<ClientBasicDTO> delete(Long id) {
        log.debug("Deleting client entity with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("Client entity with id: [{}] has been deleted", id);
                    return Optional.of(mapper.getClientDomainToDTOBasicMapper().getDestination(company));
                })
                .orElseGet(() -> {
                    log.info("Client entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public Optional<ClientBasicDTO> update(Long id, ClientBasicDTO clientDTO) throws ClientException {
        log.debug("Updating client with id: [{}]. Client data: [{}]", id, clientDTO);

        return repository
                .findById(id)
                .map((client) -> {
                    validateClientUpdate(clientDTO, client);
                    client = mapper
                            .getClientDTOToDomainMapper()
                            .getDestination(
                                    client,
                                    clientDTO,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    client.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(client);

                    log.debug("Client entity with id [{}] has been successfully updated. Updated data: [{}]", id, clientDTO);
                    return Optional.of(mapper.getClientDomainToDTOBasicMapper().getDestination(client));
                })
                .orElseGet(() -> {
                    log.info("Client entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public Optional<ClientBasicDTO> updateCSM(Long id, String userId) {
        log.debug("Updating client Customer Success Manager. Client id: [{}]. User id: [{}]", id, userId);

        UserDTO user = userService.findById(userId);
        ClientBasicDTO client = new ClientBasicDTO();
        client.setId(id);
        client.setCustomerSuccessManager(user);
        return update(id, client);
    }

    @Override
    public Optional<ClientBasicDTO> updateSA(Long id, String userId) {
        log.debug("Updating client Customer Success Manager. Client id: [{}]. User id: [{}]", id, userId);

        UserDTO user = userService.findById(userId);
        ClientBasicDTO client = new ClientBasicDTO();
        client.setId(id);
        client.setSalesAssistant(user);
        return update(id, client);
    }

    private void processCompanyData(ClientBasicDTO client, LocalDateTime datetime) {
        // Create company entry
        if (client.getCompany() != null) {
            companyService
                    .findByName(client.getCompany().getName())
                    .ifPresentOrElse(client::setCompany, () -> {
                        client.getCompany().setUpdated(datetime);
                        client.getCompany().setCreated(datetime);

                        log.debug("Company found. Client [{}] is going ot be assign to company name [{}]",
                                client.getFullName(),
                                client.getCompany().getName());
                    });
        }
    }

    private void validateClientUpdate(ClientBasicDTO clientUpdate, Client clientToBeUpdated) throws ClientException {

        // Customer Success Manager must have that role
        UserDTO csmUser = clientUpdate.getCustomerSuccessManager();
        if (csmUser != null &&
                csmUser
                        .getRoles()
                        .stream()
                        .noneMatch((role) -> role.equals(Role.CSM.name()))) {
            throw new ClientException(
                    ExceptionMessageConstants.CLIENT_UPDATE_CSM_MUST_HAVE_ROLE_CSM_EXCEPTION,
                    new String[]{
                            String.valueOf(clientUpdate.getId()),
                            csmUser.getId(),
                            csmUser.getFullName(),
                            csmUser.getRoles().toString()});
        }
        // Sales Assistant must have that role
        UserDTO saUser = clientUpdate.getSalesAssistant();
        if (saUser != null &&
                saUser.getRoles()
                        .stream()
                        .noneMatch((role) -> role.equals(Role.SA.name()))) {
            throw new ClientException(
                    ExceptionMessageConstants.CLIENT_UPDATE_SA_MUST_HAVE_ROLE_SA_EXCEPTION,
                    new String[]{
                            String.valueOf(clientUpdate.getId()),
                            saUser.getId(),
                            saUser.getFullName(),
                            saUser.getRoles().toString()});
        }

        // Customer Success Manager can not be already Sales Assistant for the same client
        if (csmUser != null && clientToBeUpdated.getSalesAssistant() != null &&
                csmUser.getId().equals(clientToBeUpdated.getSalesAssistant().getId())) {
            throw new ClientException(
                    ExceptionMessageConstants.CLIENT_UPDATE_CSM_MATCH_SA_EXCEPTION,
                    new String[]{
                            String.valueOf(clientUpdate.getId()),
                            csmUser.getId(),
                            csmUser.getFullName()});
        }

        // Sales Assistant can not be Customer Success Manager for the same client
        if (saUser != null && clientToBeUpdated.getCustomerSuccessManager() != null &&
                saUser.getId().equals(clientToBeUpdated.getCustomerSuccessManager().getId())) {
            throw new ClientException(
                    ExceptionMessageConstants.CLIENT_UPDATE_SA_MATCH_CSM_EXCEPTION,
                    new String[]{
                            String.valueOf(clientUpdate.getId()),
                            saUser.getId(),
                            saUser.getFullName()});
        }
    }
}
