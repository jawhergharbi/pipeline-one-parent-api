package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;

import java.util.List;


public interface AuthJwtService {

    AuthenticationDTO create(AuthJwtRegisterRequest authJwtRegisterRequest, String identifier) throws AuthException;

    AuthenticationDTO findById(String id) throws ResourceNotFoundException;

    AuthenticationDTO findByIdentifier(String identifier) throws ResourceNotFoundException;

    AuthenticationDTO delete(String id) throws ResourceNotFoundException;

    List<AuthenticationDTO> findAll();

    AuthenticationDTO updatePassword(String id, String password) throws ResourceNotFoundException, AuthException;

    AuthenticationDTO updateIdentifier(String id, String identifier) throws ResourceNotFoundException;
}
