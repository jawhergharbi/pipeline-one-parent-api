package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.model.lead.LeadInteraction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LeadUtils {

    protected static Optional<LeadInteraction> findLastInteraction(List<LeadInteraction> interactions, LocalDateTime datetime) {
        return interactions
                .stream()
                .filter((interaction) -> interaction.getScheduled().isBefore(datetime))
                .findFirst();
    }

    public static Optional<LeadInteraction> findNextInteraction(List<LeadInteraction> interactions, LocalDateTime datetime) {
        return interactions
                .stream()
                .filter((interaction) -> interaction.getScheduled().isAfter(datetime))
                .findFirst();
    }
}
