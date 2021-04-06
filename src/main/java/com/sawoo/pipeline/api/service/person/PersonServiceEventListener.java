package com.sawoo.pipeline.api.service.person;

import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonServiceEventListener {

    @EventListener
    public void handleBeforeSaveEvent(BaseServiceBeforeSaveEvent<PersonDTO, Person> event) {
        log.debug("Person before save listener");
        Person entity = event.getModel();
        PersonDTO dto = event.getDto();
        // Consolidate firstName and lastName
        if (entity != null && (dto.getFirstName() != null || dto.getLastName() != null)) {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : entity.getFirstName();
            String lastName = dto.getLastName() != null ? dto.getLastName() : entity.getLastName();
            entity.setFullName(String.join(" ", firstName, lastName));
        }
    }
}
