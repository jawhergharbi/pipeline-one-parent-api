package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.UserMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositoryMongo extends MongoRepository<UserMongoDB, String> {

    Optional<UserMongoDB> findByEmail(String email);

    List<UserMongoDB> findByActiveTrueAndRolesIn(List<String> roles);

}
