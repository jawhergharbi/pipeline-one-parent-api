package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterReq;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.dto.user.UserDTO;

import java.util.List;
import java.util.Optional;


public interface UserAuthJwtService {

    UserAuthDTO create(AuthJwtRegisterReq registerRequest) throws AuthException;

    UserAuthDTO delete(String id) throws ResourceNotFoundException;

    UserAuthDTO findById(String id) throws ResourceNotFoundException;

    List<UserAuthDTO> findAll();

    UserAuthDTO update(UserAuthUpdateDTO user) throws ResourceNotFoundException, AuthException;

    List<UserAuthDTO> findAllByRole(List<String> roles);
}
