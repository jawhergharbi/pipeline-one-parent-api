package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthRegister;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


public interface UserAuthJwtService {

    UserAuthDTO create(UserAuthRegister registerRequest) throws AuthException;

    UserAuthDTO delete(String id) throws ResourceNotFoundException;

    UserAuthDTO findById(String id) throws ResourceNotFoundException;

    List<UserAuthDTO> findAll();

    UserAuthDTO update(UserAuthUpdateDTO user) throws ResourceNotFoundException, AuthException;

    List<UserAuthDTO> findAllByRole(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Size(min = 1, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR) List<String> roles);

    UserAuthDetails authenticate(String email, String password) throws AuthException;
}
