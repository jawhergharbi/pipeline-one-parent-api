package com.sawoo.pipeline.api.dto.company;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyValidator implements ConstraintValidator<CompanyValid, CompanyDTO> {
    @Override
    public boolean isValid(CompanyDTO company, ConstraintValidatorContext constraintValidatorContext) {
        return company.getId() != null || (StringUtils.hasText(company.getName()) && StringUtils.hasText(company.getUrl()));
    }
}
