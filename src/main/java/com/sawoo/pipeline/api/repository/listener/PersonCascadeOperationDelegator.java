package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PersonCascadeOperationDelegator implements CascadeOperationDelegation<Person> {

    private final PersonRepository personRepository;

    @Override
    public void onSave(Person child, Consumer<Person> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                personRepository
                        .findByLinkedInUrl(child.getLinkedInUrl())
                        .ifPresentOrElse(parentFunction,
                                () -> {
                                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                    child.setCreated(now);
                                    child.setUpdated(now);
                                    personRepository.insert(child);
                                });
            } else {
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                child.setUpdated(now);
                personRepository.save(child);
            }
        }
    }
}
