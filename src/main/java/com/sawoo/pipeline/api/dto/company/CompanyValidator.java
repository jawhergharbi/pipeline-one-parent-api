package com.sawoo.pipeline.api.dto.company;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyValidator implements ConstraintValidator<CompanyValid, CompanyDTO> {
    @Override
    public boolean isValid(CompanyDTO companyDTO, ConstraintValidatorContext constraintValidatorContext) {
        return companyDTO.getId() != null || (StringUtils.hasText(companyDTO.getName()) && StringUtils.hasText(companyDTO.getUrl()));
    }
}
