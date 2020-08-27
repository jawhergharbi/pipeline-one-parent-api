package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.client.Client;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.Optional;

public interface ClientRepository extends DatastoreRepository<Client, Long> {

    Optional<Client> findByLinkedInUrl(String linkedInUrl);
}
