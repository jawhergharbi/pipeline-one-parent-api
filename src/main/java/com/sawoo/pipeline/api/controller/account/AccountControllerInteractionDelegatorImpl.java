package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("accountControllerInteraction")
public class AccountControllerInteractionDelegatorImpl implements AccountControllerInteractionDelegator {

    private final LeadService leadService;
    private final AccountService accountService;

    @Autowired
    public AccountControllerInteractionDelegatorImpl(LeadService leadService, AccountService accountService) {
        this.leadService = leadService;
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<List<LeadInteractionDTO>> findAllInteractions(
            List<String> accountIds,
            List<Integer> status,
            List<Integer> types) throws CommonServiceException {
        List<LeadDTO> leads = accountService.findAllLeads(accountIds.toArray(new String[0]), null);
        List<LeadInteractionDTO> interactions = Collections.emptyList();
        if (leads.size() > 0) {
            List<String> leadIds = leads.stream().map(LeadDTO::getId).collect(Collectors.toList());
            interactions = leadService.findBy(leadIds, status, types);
        }
        return ResponseEntity.ok().body(interactions);
    }
}
