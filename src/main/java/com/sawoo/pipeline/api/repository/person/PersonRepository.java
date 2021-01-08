package com.sawoo.pipeline.api.repository.person;

import com.sawoo.pipeline.api.model.person.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {

    Optional<Person> findByLinkedInUrl(String linkedInUrl);
}
