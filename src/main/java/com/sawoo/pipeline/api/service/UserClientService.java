package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;

import java.util.List;

public interface UserClientService {

    List<ClientBasicDTO> findAll(String id) throws ResourceNotFoundException;
}
