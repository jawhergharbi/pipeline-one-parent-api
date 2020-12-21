package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadInteractionRepository extends MongoRepository<LeadInteraction, String> {
}
