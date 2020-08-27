package com.sawoo.pipeline.api.service;


import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientServiceUtils {

    public Optional<Lead> getNextInteraction(List<Lead> leads, LocalDateTime datetime) {
        Optional<LeadInteraction> leadInteraction = leads.stream()
                .flatMap((lead) -> lead.getInteractions().stream())
                .sorted(Comparator.comparing(LeadInteraction::getScheduled))
                .collect(Collectors.toList())
                .stream()
                .filter((interaction) -> interaction.getScheduled().isAfter(datetime))
                .findFirst();
        return leadInteraction
                .flatMap(interaction -> leads.stream().filter((lead) -> lead.getInteractions().contains(interaction))
                        .findFirst());
    }
}
