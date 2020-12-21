package com.sawoo.pipeline.api.dto.prospect;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProspectValidator implements ConstraintValidator<ProspectValid, ProspectDTO> {
    @Override
    public boolean isValid(ProspectDTO prospect, ConstraintValidatorContext constraintValidatorContext) {
        return prospect != null && (prospect.getId() != null ||
                (StringUtils.hasText(prospect.getFirstName()) &&
                        StringUtils.hasText(prospect.getLastName()) &&
                        StringUtils.hasText(prospect.getLinkedInUrl()) &&
                        StringUtils.hasText(prospect.getEmail()) &&
                        StringUtils.hasText(prospect.getPosition()) &&
                        prospect.getCompany() != null));
    }
}
