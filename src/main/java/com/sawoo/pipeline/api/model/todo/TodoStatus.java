package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;
import com.sawoo.pipeline.api.common.validation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TodoStatus implements IEnum<Integer> {

    PENDING(0),
    CANCELLED(1),
    COMPLETED(2),
    ON_GOING(3),
    UNASSIGNED(4);

    private final Integer value;

    public static TodoStatus fromValue(int value) {
        return Arrays
                .stream(TodoStatus.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {TodoStatus.class.getSimpleName(), TodoStatus.values(), value}));
    }
}
