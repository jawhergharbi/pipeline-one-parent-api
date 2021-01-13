package com.sawoo.pipeline.api.service.person;

import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import org.springframework.stereotype.Component;

@Component
public class PersonServiceEventListener implements BaseServiceEventListener<PersonDTO, Person> {

    @Override
    public void onBeforeInsert(PersonDTO dto, Person entity) {
        // nothing to do atm
    }

    @Override
    public void onBeforeSave(PersonDTO dto, Person entity) {
        // Consolidate firstName and lastName
        if (entity != null && (dto.getFirstName() != null || dto.getLastName() != null)) {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : entity.getFirstName();
            String lastName = dto.getLastName() != null ? dto.getLastName() : entity.getLastName();
            entity.setFullName(String.join(" ", firstName, lastName));
        }
    }

    @Override
    public void onBeforeUpdate(PersonDTO dto, Person entity) {
        // nothing to do atm
    }
}
