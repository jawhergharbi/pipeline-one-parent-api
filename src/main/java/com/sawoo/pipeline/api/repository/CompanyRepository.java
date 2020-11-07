package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.CompanyMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyMongoDB, String> {

    Optional<CompanyMongoDB> findByName(String name);
}
