package com.sawoo.pipeline.api.repository.company;

import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CompanyRepository extends BaseMongoRepository<Company> {

    Optional<Company> findByName(String name);
}
