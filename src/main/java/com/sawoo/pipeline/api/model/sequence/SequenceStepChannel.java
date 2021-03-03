package com.sawoo.pipeline.api.model.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;

import java.util.Arrays;

public enum SequenceStepChannel {

    LINKED_IN(0),
    EMAIL(10),
    SMS(20),
    WHATSAPP(30);

    private final int value;

    SequenceStepChannel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SequenceStepChannel fromValue(int value) {
        return Arrays
                .stream(SequenceStepChannel.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {SequenceStepChannel.class.getSimpleName(), SequenceStepChannel.values(), value}));
    }
}
