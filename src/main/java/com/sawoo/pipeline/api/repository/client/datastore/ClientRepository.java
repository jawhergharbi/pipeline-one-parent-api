package com.sawoo.pipeline.api.repository.client.datastore;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.model.client.Client;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends DatastoreRepository<Client, Long> {

    Optional<Client> findByLinkedInUrl(String linkedInUrl);

    @Query("SELECT * FROM client WHERE customerSuccessManager = @csm_id")
    List<Client> findByCSMIs(@Param("csm_id") Key csmKey);

    @Query("SELECT * FROM client WHERE salesAssistant = @sa_id")
    List<Client> findBySAIs(@Param("sa_id") Key saKey);
}
