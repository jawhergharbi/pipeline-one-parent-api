package com.sawoo.pipeline.api.common.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserClientException extends RuntimeException {

    private final String message;
    private final Object[] args;
}
