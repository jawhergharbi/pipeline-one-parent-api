package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.Authentication;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.Optional;

public interface AuthRepository extends DatastoreRepository<Authentication, String> {

    Optional<Authentication> findByIdentifier(String identifier);
}
