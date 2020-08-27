package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.Company;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.Optional;

public interface CompanyRepository extends DatastoreRepository<Company, Long> {

    Optional<Company> findByName(String name);
}
