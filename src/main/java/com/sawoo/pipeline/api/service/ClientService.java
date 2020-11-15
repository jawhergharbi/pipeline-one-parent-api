package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;

import java.util.Optional;

public interface ClientService {

    Optional<ClientBasicDTO> update(Long id, ClientBasicDTO client) throws ClientException;

    Optional<ClientBasicDTO> updateCSM(Long id, String userId) throws ClientException;

    Optional<ClientBasicDTO> updateSA(Long id, String userId) throws ClientException;
}
