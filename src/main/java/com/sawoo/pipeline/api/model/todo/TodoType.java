package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;
import com.sawoo.pipeline.api.common.validation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TodoType implements IEnum<Integer> {

    OUT_GOING_INTERACTION(0),
    IN_COMING_INTERACTION(1);

    private final Integer value;

    public static TodoType fromValue(int value) {
        return Arrays
                .stream(TodoType.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {TodoType.class.getSimpleName(), TodoType.values(), value}));
    }
}
