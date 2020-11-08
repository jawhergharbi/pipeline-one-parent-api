package com.sawoo.pipeline.api.repository.interaction;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.model.prospect.LeadInteraction;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeadInteractionRepository extends DatastoreRepository<LeadInteraction, Key> {

    @Query("SELECT * FROM interaction WHERE __key__ HAS ANCESTOR KEY(lead, @lead_id)")
    List<LeadInteraction> findAllByParent(@Param("parent_id") String leadId);
}
