package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.lead.LeadInteraction;

import java.util.List;
import java.util.Optional;

public interface LeadInteractionRepositoryWrapper {

    LeadInteraction save(Long leadId, LeadInteraction interaction);

    LeadInteraction save(LeadInteraction interaction);

    Optional<LeadInteraction> findById(Long leadId, Long interactionId);

    Optional<LeadInteraction> deleteById(Long leadId, Long interactionId);
}
