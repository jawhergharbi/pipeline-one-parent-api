package com.sawoo.pipeline.api.dto.auth.register;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthJwtRegisterRequestBase extends AuthJwtRegisterRequest {

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String identifier;

    @Builder
    public AuthJwtRegisterRequestBase(String identifier, String password, String confirmPassword, String fullName, String role, Integer providerType) {
        super(password, confirmPassword, fullName, role, providerType);
        this.identifier = identifier;
    }

}
