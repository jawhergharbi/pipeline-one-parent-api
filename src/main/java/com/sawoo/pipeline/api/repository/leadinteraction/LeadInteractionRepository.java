package com.sawoo.pipeline.api.repository.leadinteraction;

import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadInteractionRepository extends MongoRepository<LeadInteraction, String>, LeadInteractionRepositoryCustom {

    List<LeadInteraction> findByLeadId(String leadId);

    List<LeadInteraction> findByLeadIdIn(List<String> leadId);
}
