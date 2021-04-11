package com.sawoo.pipeline.api.repository.person;

import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.Optional;

@JaversSpringDataAuditable
public interface PersonRepository extends BaseMongoRepository<Person> {

    Optional<Person> findByLinkedInUrl(String linkedInUrl);
}
