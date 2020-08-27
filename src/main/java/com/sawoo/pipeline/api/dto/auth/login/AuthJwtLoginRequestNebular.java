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
public class AuthJwtLoginRequestNebular extends AuthJwtLoginRequest {

    private static final long serialVersionUID = 7165877998272755619L;
    private String email;

    @Builder
    public AuthJwtLoginRequestNebular(String email, String password) {
        super(password);
        this.email = email;
    }
}
