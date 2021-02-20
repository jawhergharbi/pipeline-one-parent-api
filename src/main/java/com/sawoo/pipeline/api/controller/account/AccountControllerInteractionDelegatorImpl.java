package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            interactions = interactions
                    .stream()
                    .peek( (i) -> mapAccountData(leads, i))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(interactions);
    }

    private void mapAccountData(List<LeadDTO> leads, LeadInteractionDTO interaction) {
        Optional<LeadDTO> lead = leads.stream().filter(l -> l.getId().equals(interaction.getLead().getLeadId())).findAny();
        lead.ifPresent(l -> {
            AccountFieldDTO account = l.getAccount();
            interaction.setAccount(account);
            if (interaction.getAssigneeId() != null) {
                Optional<UserAuthDTO> assignee = account.getUsers()
                        .stream()
                        .filter(u -> u.getId().equals(interaction.getAssigneeId())).findAny();
                assignee.ifPresent(a -> interaction.setAssignee(UserCommon.builder()
                        .fullName(a.getFullName())
                        .id(a.getId())
                        .type(UserCommonType.USER)
                        .build()));
            }
        });
    }
}
