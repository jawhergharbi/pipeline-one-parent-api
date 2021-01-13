package com.sawoo.pipeline.api.repository.company;

import com.sawoo.pipeline.api.model.company.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByName(String name);
}