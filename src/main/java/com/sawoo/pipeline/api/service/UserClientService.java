package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;

import java.util.List;

public interface UserClientService {

    ClientBasicDTO create(String id, ClientBasicDTO client) throws CommonServiceException;

    ClientBasicDTO add(String id, Long clientId) throws ResourceNotFoundException, UserClientException;

    List<ClientBasicDTO> findAll(String id);

    ClientBasicDTO remove(String id, Long clientId) throws ResourceNotFoundException, UserClientException;
}
