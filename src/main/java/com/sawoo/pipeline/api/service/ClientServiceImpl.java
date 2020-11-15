package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import com.sawoo.pipeline.api.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepositoryWrapper repository;
    private final CommonServiceMapper mapper;
    private final CompanyService companyService;

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

        /*UserDTO user = userService.findById(userId);
        ClientBasicDTO client = new ClientBasicDTO();
        client.setId(id);
        client.setCustomerSuccessManager(user);
        return update(id, client);*/
        return null;
    }

    @Override
    public Optional<ClientBasicDTO> updateSA(Long id, String userId) {
        log.debug("Updating client Customer Success Manager. Client id: [{}]. User id: [{}]", id, userId);

        /*UserDTO user = userService.findById(userId);
        ClientBasicDTO client = new ClientBasicDTO();
        client.setId(id);
        client.setSalesAssistant(user);
        return update(id, client);*/
        return null;
    }

    private void validateClientUpdate(ClientBasicDTO clientUpdate, Client clientToBeUpdated) throws ClientException {

        // Customer Success Manager must have that role
        UserDTOOld csmUser = clientUpdate.getCustomerSuccessManager();
        if (csmUser != null &&
                csmUser
                        .getRoles()
                        .stream()
                        .noneMatch((role) -> role.equals(Role.MNG.name()))) {
            throw new ClientException(
                    ExceptionMessageConstants.CLIENT_UPDATE_CSM_MUST_HAVE_ROLE_CSM_EXCEPTION,
                    new String[]{
                            String.valueOf(clientUpdate.getId()),
                            csmUser.getId(),
                            csmUser.getFullName(),
                            csmUser.getRoles().toString()});
        }
        // Sales Assistant must have that role
        UserDTOOld saUser = clientUpdate.getSalesAssistant();
        if (saUser != null &&
                saUser.getRoles()
                        .stream()
                        .noneMatch((role) -> role.equals(Role.AST.name()))) {
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
