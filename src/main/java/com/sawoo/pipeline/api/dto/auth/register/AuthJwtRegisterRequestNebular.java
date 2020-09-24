package com.sawoo.pipeline.api.dto.auth.register;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthJwtRegisterRequestNebular extends AuthJwtRegisterRequest {

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @Builder
    public AuthJwtRegisterRequestNebular(String email, String password, String confirmPassword, String fullName, String role, Integer providerType) {
        super(password, confirmPassword, fullName, role, providerType);
        this.email = email;
    }

}
