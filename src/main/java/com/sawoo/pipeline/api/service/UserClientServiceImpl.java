package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.UserRepositoryOld;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final UserRepositoryOld userRepositoryOld;
    private final ClientRepositoryWrapper clientRepository;
    private final CommonServiceMapper mapper;

    @Override
    public List<ClientBasicDTO> findAll(String id) throws ResourceNotFoundException {
        log.debug("Retrieve clients for user id [{}]", id);

        return userRepositoryOld
                .findById(id)
                .map((user) -> {
                    List<Client> clients;
                    if (user.getRoles().contains(Role.ADMIN.name())) {
                        clients = StreamSupport
                                .stream(clientRepository.findAll().spliterator(), false)
                                .collect(Collectors.toList());
                    } else {
                        clients = clientRepository.findByUserId(id);
                    }
                    log.debug("[{}] client/s has/have been found for user id: [{}]", clients.size(), id);
                    return clients
                            .stream()
                            .map((client) -> mapper.getClientDomainToDTOBasicMapper().getDestination(client))
                            .collect(Collectors.toList());
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"User", id})
                );
    }
}
