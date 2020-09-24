package com.sawoo.pipeline.api.dto.auth.update;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthJwtUpdateEmailRequest extends AuthJwtUpdateRequest {

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @Builder
    public AuthJwtUpdateEmailRequest(String id, String email) {
        super(id);
        this.email = email;
    }

}
