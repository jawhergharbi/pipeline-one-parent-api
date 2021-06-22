package com.sawoo.pipeline.api.common.contants;

public interface JwtConstants {
    String AUTHORIZATION_HEADER_KEY = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    long TOKEN_VALIDITY = 5 * 60 * 60;
}
