package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientService {

    ClientBasicDTO create(ClientBasicDTO client) throws CommonServiceException;

    Optional<ClientBasicDTO> findById(Long id);

    List<ClientBasicDTO> findAll();

    List<ClientBasicDTO> findAllMain(LocalDateTime datetime);

    Optional<ClientBasicDTO> delete(Long id);

    Optional<ClientBasicDTO> update(Long id, ClientBasicDTO client) throws ClientException;

    Optional<ClientBasicDTO> updateCSM(Long id, String userId) throws ClientException;

    Optional<ClientBasicDTO> updateSA(Long id, String userId) throws ClientException;
}
