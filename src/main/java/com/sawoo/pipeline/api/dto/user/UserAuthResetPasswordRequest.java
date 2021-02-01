package com.sawoo.pipeline.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sawoo.pipeline.api.common.contants.CommonConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(value = {"password", "confirmPassword"}, allowSetters = true)
public class UserAuthResetPasswordRequest {

    @Size(min = CommonConstants.AUTH_PASSWORD_MIN_LENGTH,
            message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String password;

    @Size(min = CommonConstants.AUTH_PASSWORD_MIN_LENGTH,
            message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String confirmPassword;

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String token;
}
