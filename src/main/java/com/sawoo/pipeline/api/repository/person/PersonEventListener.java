package com.sawoo.pipeline.api.repository.person;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.listener.CompanyCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonEventListener extends AbstractMongoEventListener<Person> {

    private final CompanyCascadeOperationDelegator companyCascadeDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Person> event) {
        Person person = event.getSource();
        // Person salutation
        if (person.getSalutation() == null) {
            person.setSalutation(DomainConstants.SALUTATION_EMPTY);
        }

        // Consolidate fullName
        person.setFullName(String.join(" ", person.getFirstName(), person.getLastName()));

        // Company delegator
        companyCascadeDelegator.onSave(person.getCompany(), person::setCompany);
        super.onBeforeConvert(event);
    }
}
