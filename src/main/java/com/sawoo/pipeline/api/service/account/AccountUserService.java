package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface AccountUserService {

    List<AccountDTO> findAllByUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException;

    AccountDTO updateUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException;
}
