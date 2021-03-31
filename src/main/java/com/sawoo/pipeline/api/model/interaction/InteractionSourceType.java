package com.sawoo.pipeline.api.model.interaction;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum InteractionSourceType {

    MANUAL(0),
    AUTOMATIC(1);

    private final int value;

    InteractionSourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InteractionSourceType fromValue(int value) {
        return Arrays
                .stream(InteractionSourceType.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {InteractionSourceType.class.getSimpleName(), InteractionSourceType.values(), value}));
    }
}
