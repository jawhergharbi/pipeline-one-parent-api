package com.sawoo.pipeline.api.model.common;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum TodoChannel {

    LINKED_IN(0),
    EMAIL(1),
    PHONE(2),
    TASK(3),
    LINKED_IN_AUDIO(5),
    SMS(6),
    WHATS_APP(7),
    WHATS_APP_AUDIO(8),
    REMINDER(9);

    private final int value;

    TodoChannel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static TodoChannel fromValue(int value) {
        return Arrays
                .stream(TodoChannel.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {TodoChannel.class.getSimpleName(), TodoChannel.values(), value}));
    }
}
