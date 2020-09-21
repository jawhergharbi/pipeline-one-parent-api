package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.UserRepository;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final UserRepository userRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final CommonServiceMapper mapper;

    @Override
    public ClientBasicDTO create(String id, ClientBasicDTO client) {
        return null;
    }

    @Override
    public ClientBasicDTO add(String id, Long clientId) throws UserClientException, ResourceNotFoundException {
        log.debug("Add client id [{}] to user id [{}]", clientId, id);
        return userRepository.findById(id)
                .map((user) -> clientRepository.findById(clientId)
                        .map((client) -> {

                            Optional<Role> userOpsRole = UserUtils.getOpsRole(user.getRoles());
                            if (userOpsRole.isEmpty()) {
                                throw new UserClientException(
                                        ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_USER_NO_OPS_ROLE_EXCEPTION,
                                        new String[]{String.valueOf(client.getId()), client.getFullName(), user.getId(), user.getFullName(), user.getRoles().toString()});
                            }

                            Role opsRole = userOpsRole.get();
                            validateDuplicatedRole(client, user, opsRole);

                            User oldUser = getAssignedUser(client, opsRole);
                            if (oldUser != null) {
                                if (oldUser.getId().equals(user.getId())) {
                                    throw new UserClientException(
                                            ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_USER_ALREADY_ADDED_EXCEPTION,
                                            new String[]{opsRole.name(), String.valueOf(client.getId()), client.getFullName(), user.getId(), user.getFullName(), Role.CSM.name()});
                                }
                                remove(oldUser.getId(), clientId);
                            }

                            user.getClients().add(client);
                            user.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                            userRepository.save(user);

                            Client updatedClient = updateClient(client, user, opsRole);

                            log.debug("User id [{}] has been assigned to client id [{}] with role [{}]",
                                    user.getId(),
                                    client.getId(),
                                    opsRole);

                            return mapper.getClientDomainToDTOBasicMapper().getDestination(updatedClient);

                        })
                        .orElseThrow(() -> new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Client", String.valueOf(clientId)})))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"User", id}));
    }

    @Override
    public List<ClientBasicDTO> findAll(String id) {
        log.debug("Retrieve clients for user id [{}]", id);

        List<ClientBasicDTO> clients = clientRepository
                .findByUserId(id)
                .stream()
                .map((client) -> mapper.getClientDomainToDTOBasicMapper().getDestination(client))
                .collect(Collectors.toList());

        log.debug("[{}] client/s has/have been found for user id: [{}]", clients.size(), id);

        return  clients;
    }

    @Override
    public ClientBasicDTO remove(String id, Long clientId) throws UserClientException, ResourceNotFoundException {
        log.debug("Remove client id [{}] from user id [{}]", clientId, id);
        return userRepository
                .findById(id)
                .map((user) -> clientRepository.findById(clientId)
                        .map((client) -> {

                            user.getClients().remove(client);
                            user.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                            userRepository.save(user);

                            Optional<Role> userOpsRole = UserUtils.getOpsRole(user.getRoles());
                            if (userOpsRole.isEmpty()) {
                                throw new UserClientException(
                                        ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_USER_NO_OPS_ROLE_EXCEPTION,
                                        new String[]{
                                                String.valueOf(client.getId()),
                                                client.getFullName(),
                                                user.getId(),
                                                user.getFullName(),
                                                user.getRoles().toString()
                                        });
                            }
                            Client updatedClient = updateClient(client, null, userOpsRole.get());

                            log.debug("User id [{}] with role [{}] has been remove from client id [{}] ",
                                    user.getId(),
                                    userOpsRole,
                                    client.getId());

                            return mapper.getClientDomainToDTOBasicMapper().getDestination(updatedClient);
                        })
                        .orElseThrow(() -> new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Client", String.valueOf(clientId)})))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"User", id}));
    }

    private User getAssignedUser(Client client, Role role) {
        return role.equals(Role.SA) ? client.getSalesAssistant() :
                role.equals(Role.CSM) ? client.getCustomerSuccessManager() : null;
    }

    private Client updateClient(Client client, User user, Role role) {
        if (role.equals(Role.SA)) {
            client.setSalesAssistant(user);
        } else if (role.equals(Role.CSM)) {
            client.setCustomerSuccessManager(user);
        }
        client.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return clientRepository.save(client);
    }

    private void validateDuplicatedRole(Client client, User user, Role role) throws UserClientException {
        if (role.equals(Role.SA) &&
                client.getCustomerSuccessManager() != null &&
                client.getCustomerSuccessManager().getId().equals(user.getId())) {
            throw new UserClientException(ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_DUPLICATED_ROLE_EXCEPTION,
                    new String[]{
                            role.name(),
                            String.valueOf(client.getId()),
                            client.getFullName(),
                            user.getId(),
                            user.getFullName(),
                            Role.CSM.name()});
        } else if (role.equals(Role.CSM) &&
                client.getSalesAssistant() != null &&
                client.getSalesAssistant().getId().equals(user.getId())) {
            throw new UserClientException(ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_DUPLICATED_ROLE_EXCEPTION,
                    new String[]{
                            role.name(),
                            String.valueOf(client.getId()),
                            client.getFullName(),
                            user.getId(),
                            user.getFullName(),
                            Role.SA.name()});
        }
    }

    private List<Client> getClients(User user) {
        if (user.getRoles().contains(Role.ADMIN.name())) {
            return StreamSupport
                    .stream(clientRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
        } else if (user.getRoles().contains(Role.CSM.name())) {
            return Stream
                    .concat(
                            user.getClients().stream(),
                            StreamSupport
                                    .stream(clientRepository.findAll().spliterator(), false)
                                    .filter( (client) -> client.getCustomerSuccessManager() == null))
                    .collect(Collectors.toList());
        } else if (user.getRoles().contains(Role.SA.name())) {
            return user.getClients();
        } else {
            return Collections.emptyList();
        }
    }
}
