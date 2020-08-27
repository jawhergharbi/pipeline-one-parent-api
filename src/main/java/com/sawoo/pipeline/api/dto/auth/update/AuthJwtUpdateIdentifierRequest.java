package com.sawoo.pipeline.api.dto.auth.update;

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
public class AuthJwtUpdateIdentifierRequest extends AuthJwtUpdateRequest {

    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String identifier;

    @Builder
    public AuthJwtUpdateIdentifierRequest(String id, String identifier) {
        super(id);
        this.identifier = identifier;
    }

}
