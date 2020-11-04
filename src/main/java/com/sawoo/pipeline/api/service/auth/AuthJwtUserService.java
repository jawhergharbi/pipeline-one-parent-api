package com.sawoo.pipeline.api.service.auth;

import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterReq;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;

import java.util.List;


public interface AuthJwtUserService {

    AuthenticationDTO create(AuthJwtRegisterRequest authJwtRegisterRequest, String identifier) throws AuthException;

    UserAuthDTO create(AuthJwtRegisterReq registerRequest) throws AuthException;

    AuthenticationDTO findById(String id) throws ResourceNotFoundException;

    AuthenticationDTO findByIdentifier(String identifier) throws ResourceNotFoundException;

    AuthenticationDTO delete(String id) throws ResourceNotFoundException;

    List<AuthenticationDTO> findAll();

    AuthenticationDTO updatePassword(String id, String password) throws ResourceNotFoundException, AuthException;

    AuthenticationDTO updateIdentifier(String id, String identifier) throws ResourceNotFoundException;
}
