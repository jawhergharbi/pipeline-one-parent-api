package com.sawoo.pipeline.api.dto.auth.register;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class AuthJwtRegisterReq {

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @Size(min = 6, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String password;

    @Size(min = 6, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String confirmPassword;

    @Size(max = 100, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_EXCEED_MAX_SIZE_ERROR)
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String fullName;

    private String role;
}
