package com.sawoo.pipeline.api.dto.person;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonValidator implements ConstraintValidator<PersonValid, PersonDTO> {
    @Override
    public boolean isValid(PersonDTO person, ConstraintValidatorContext constraintValidatorContext) {
        return person != null && (person.getId() != null ||
                (StringUtils.hasText(person.getFirstName()) &&
                        StringUtils.hasText(person.getLastName()) &&
                        StringUtils.hasText(person.getLinkedInUrl()) &&
                        StringUtils.hasText(person.getEmail()) &&
                        StringUtils.hasText(person.getPosition()) &&
                        person.getCompany() != null));
    }
}
