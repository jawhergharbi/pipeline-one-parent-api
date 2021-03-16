package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.model.user.User;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    List<User> findByActiveTrueAndRolesIn(List<String> roles);

}
