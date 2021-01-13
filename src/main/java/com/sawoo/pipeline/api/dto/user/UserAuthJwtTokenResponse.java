package com.sawoo.pipeline.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAuthJwtTokenResponse {

    private static final long serialVersionUID = 3985851303906067903L;

    private final String token;
}
