package com.sawoo.pipeline.api.dto.company;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CompanyValidator.class)
public @interface CompanyValid {
    String message() default "{company.validation.cross-field.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
