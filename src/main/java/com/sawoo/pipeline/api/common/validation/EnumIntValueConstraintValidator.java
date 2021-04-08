package com.sawoo.pipeline.api.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumIntValueConstraintValidator implements ConstraintValidator<EnumIntValue, Integer> {

    private boolean nullable;
    private Set<Integer> values;

    @Override
    public void initialize(EnumIntValue constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
        Class<? extends IEnum<Integer>> enumClass = constraintAnnotation.enumCLass();
        int[] exclusion = constraintAnnotation.exclusion();

        values = new HashSet<>();
        IEnum<Integer>[] enumConstants = enumClass.getEnumConstants();
        List<Integer> exclusions = Arrays.stream(exclusion).boxed().collect(Collectors.toList());
        Arrays.stream(enumConstants).filter(i -> !exclusions.contains(i.getValue())).forEach(i -> values.add(i.getValue()));
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return nullable && value == null || values.contains(value);
    }
}
