package com.sawoo.pipeline.api.dto.auth.login;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthJwtLoginRequestBase extends AuthJwtLoginRequest {

    private static final long serialVersionUID = -841402509044217564L;
    private String identifier;

    @Builder
    public AuthJwtLoginRequestBase(String identifier, String password) {
        super(password);
        this.identifier = identifier;
    }
}
