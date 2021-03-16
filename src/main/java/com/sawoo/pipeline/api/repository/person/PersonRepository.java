package com.sawoo.pipeline.api.repository.person;

import com.sawoo.pipeline.api.model.person.Person;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@JaversSpringDataAuditable
public interface PersonRepository extends MongoRepository<Person, String> {

    Optional<Person> findByLinkedInUrl(String linkedInUrl);
}
