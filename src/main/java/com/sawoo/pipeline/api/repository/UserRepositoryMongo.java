package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositoryMongo extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    List<User> findByActiveTrueAndRolesIn(List<String> roles);

}
