package com.sawoo.pipeline.api.repository.leadinteraction;

import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;

import java.util.Optional;

public interface LeadInteractionRepositoryWrapper {

    LeadInteractionOld save(Long leadId, LeadInteractionOld interaction);

    LeadInteractionOld save(LeadInteractionOld interaction);

    Optional<LeadInteractionOld> findById(Long leadId, Long interactionId);

    Optional<LeadInteractionOld> deleteById(Long leadId, Long interactionId);
}
