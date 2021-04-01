package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum TodoStatusList {

    SCHEDULED(0),
    CANCELLED(1),
    DONE(2),
    RESCHEDULED(3);

    private final int value;

    TodoStatusList(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static TodoStatusList fromValue(int value) {
        return Arrays
                .stream(TodoStatusList.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {TodoStatusList.class.getSimpleName(), TodoStatusList.values(), value}));
    }
}
