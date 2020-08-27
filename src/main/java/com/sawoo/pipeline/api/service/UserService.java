package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.user.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO create(UserDTO userDTO) throws UserException;

    Optional<UserDTO> update(String id, UserDTO userDTO);

    List<UserDTO> findAll();

    List<UserDTO> findAllByRoles(List<String> roles);

    UserDTO findById(String id);

    Optional<UserDTO> delete(String id) throws ResourceNotFoundException;
}
