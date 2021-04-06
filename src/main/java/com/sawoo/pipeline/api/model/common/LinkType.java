package com.sawoo.pipeline.api.model.common;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum LinkType {

    MEETING(0),
    ATTACHMENT(1),
    PLAIN_LINK(2),
    EMBEDDED_LINK(3),
    OTHER(10);

    private final int value;

    LinkType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static LinkType fromValue(int value) {
        return Arrays
                .stream(LinkType.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {LinkType.class.getSimpleName(), LinkType.values(), value}));
    }
}
