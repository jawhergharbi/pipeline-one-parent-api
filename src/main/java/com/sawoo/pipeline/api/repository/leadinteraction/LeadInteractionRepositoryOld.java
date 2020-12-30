package com.sawoo.pipeline.api.repository.leadinteraction;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeadInteractionRepositoryOld extends DatastoreRepository<LeadInteractionOld, Key> {

    @Query("SELECT * FROM interaction WHERE __key__ HAS ANCESTOR KEY(lead, @lead_id)")
    List<LeadInteractionOld> findAllByParent(@Param("parent_id") String leadId);
}
