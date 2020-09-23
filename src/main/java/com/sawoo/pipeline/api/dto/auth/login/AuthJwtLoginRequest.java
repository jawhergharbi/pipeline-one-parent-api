package com.sawoo.pipeline.api.dto.auth.login;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthJwtLoginRequest {

    @Size(min = 6, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String password;
}
