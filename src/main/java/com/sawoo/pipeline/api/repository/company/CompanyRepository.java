package com.sawoo.pipeline.api.repository.company;

import com.sawoo.pipeline.api.model.company.Company;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByName(String name);
}
