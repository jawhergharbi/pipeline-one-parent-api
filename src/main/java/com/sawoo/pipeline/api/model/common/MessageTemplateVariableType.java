package com.sawoo.pipeline.api.model.common;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum MessageTemplateVariableType {

    CONTEXT(0),
    RESEARCH(1),
    OTHER(10);

    private final int value;

    MessageTemplateVariableType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static MessageTemplateVariableType fromValue(int value) {
        return Arrays
                .stream(MessageTemplateVariableType.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {MessageTemplateVariableType.class.getSimpleName(), MessageTemplateVariableType.values(), value}));
    }
}
