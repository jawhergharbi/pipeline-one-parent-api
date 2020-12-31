package com.sawoo.pipeline.api.dto.prospect;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProspectValidator.class)
public @interface ProspectValid {
    String message() default "{prospect.validation.cross-field.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
