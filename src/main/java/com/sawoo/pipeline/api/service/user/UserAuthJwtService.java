package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.register.UserAuthRegister;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;

import java.util.List;


public interface UserAuthJwtService {

    UserAuthDTO create(UserAuthRegister registerRequest) throws AuthException;

    UserAuthDTO delete(String id) throws ResourceNotFoundException;

    UserAuthDTO findById(String id) throws ResourceNotFoundException;

    List<UserAuthDTO> findAll();

    UserAuthDTO update(UserAuthUpdateDTO user) throws ResourceNotFoundException, AuthException;

    List<UserAuthDTO> findAllByRole(List<String> roles);

    UserAuthDetails authenticate(String email, String password) throws AuthException;
}
