package com.sawoo.pipeline.api.common.validation;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumIntValueConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface EnumIntValue {

    String message() default ExceptionMessageConstants.COMMON_ILLEGAL_ENUMERATION_VALUE_EXCEPTION;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends IEnum<Integer>> enumCLass();

    boolean nullable() default true;

    int[] exclusion() default {};
}
