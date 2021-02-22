package com.sawoo.pipeline.api.model.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum SequenceStatus {

    IN_PROGRESS(0),
    TO_BE_REVIEWED(1),
    READY(2),
    ARCHIVED(3);

    private final int value;

    SequenceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SequenceStatus fromValue(int value) {
        return Arrays
                .stream(SequenceStatus.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {SequenceStatus.class.getSimpleName(), SequenceStatus.values(), value}));
    }
}
