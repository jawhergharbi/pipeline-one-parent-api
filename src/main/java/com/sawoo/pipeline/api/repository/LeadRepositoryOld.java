package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.prospect.LeadOld;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.Optional;

public interface LeadRepositoryOld extends DatastoreRepository<LeadOld, Long> {

    Optional<LeadOld> findByLinkedInUrl(String linkedInUrl);
}
