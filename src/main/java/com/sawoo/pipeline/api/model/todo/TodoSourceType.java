package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum TodoSourceType {

    MANUAL(0),
    AUTOMATIC(1);

    private final int value;

    TodoSourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TodoSourceType fromValue(int value) {
        return Arrays
                .stream(TodoSourceType.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {TodoSourceType.class.getSimpleName(), TodoSourceType.values(), value}));
    }
}
