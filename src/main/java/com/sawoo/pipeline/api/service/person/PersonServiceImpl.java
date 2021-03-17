package com.sawoo.pipeline.api.service.person;


import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.person.PersonRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class PersonServiceImpl extends BaseServiceImpl<PersonDTO, Person, PersonRepository, PersonMapper> implements PersonService {

    @Autowired
    public PersonServiceImpl(PersonRepository repository,
                             PersonMapper mapper,
                             ApplicationEventPublisher publisher,
                             AuditService audit) {
        super(repository, mapper, DBConstants.PERSON_DOCUMENT, publisher, audit);
    }

    @Override
    public Optional<Person> entityExists(PersonDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, linkedIn: {}]",
                DBConstants.PERSON_DOCUMENT,
                entityToCreate.getLinkedInUrl());
        return getRepository().findByLinkedInUrl(entityToCreate.getLinkedInUrl());
    }
}
