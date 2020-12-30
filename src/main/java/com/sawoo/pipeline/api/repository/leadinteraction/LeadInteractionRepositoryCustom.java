package com.sawoo.pipeline.api.repository.leadinteraction;

import com.sawoo.pipeline.api.model.lead.LeadInteraction;

import java.util.List;

public interface LeadInteractionRepositoryCustom {

    List<LeadInteraction> findBy(Integer status, Integer type, List<String> leadIds);

    List<LeadInteraction> findByStatusTypeLeads(List<Integer> status, List<Integer> type, List<String> leadIds);
}
