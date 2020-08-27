package com.sawoo.pipeline.api.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenWrapper {

    private String username;
    private String token;
}
