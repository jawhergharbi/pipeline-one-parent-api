package com.sawoo.pipeline.api.dto.auth.update;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthJwtUpdatePasswordRequest extends AuthJwtUpdateRequest {

    @Size(min = 6, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String password;

    @Builder
    public AuthJwtUpdatePasswordRequest(String id, String password) {
        super(id);
        this.password = password;
    }

}
