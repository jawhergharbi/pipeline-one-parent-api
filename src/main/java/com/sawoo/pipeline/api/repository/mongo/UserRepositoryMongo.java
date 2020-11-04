package com.sawoo.pipeline.api.repository.mongo;

import com.sawoo.pipeline.api.model.UserMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryMongo extends MongoRepository<UserMongoDB, String> {

    Optional<UserMongoDB> findByEmail(String email);

}
