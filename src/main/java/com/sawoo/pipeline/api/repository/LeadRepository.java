package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.prospect.Lead;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.Optional;

public interface LeadRepository extends DatastoreRepository<Lead, Long> {

    Optional<Lead> findByLinkedInUrl(String linkedInUrl);
}
