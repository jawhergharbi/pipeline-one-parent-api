package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTodoDTO;
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
@Qualifier("accountControllerTODO")
public class AccountControllerTodoDelegatorImpl implements AccountControllerTodoDelegator {

    private final LeadService leadService;
    private final AccountService accountService;

    @Autowired
    public AccountControllerTodoDelegatorImpl(LeadService leadService, AccountService accountService) {
        this.leadService = leadService;
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<List<LeadTodoDTO>> findAllTODOs(
            List<String> accountIds,
            List<Integer> status,
            List<Integer> types) throws CommonServiceException {
        List<LeadDTO> leads = accountService.findAllLeads(accountIds.toArray(new String[0]), null);
        List<LeadTodoDTO> todos = Collections.emptyList();
        if (!leads.isEmpty()) {
            List<String> leadIds = leads.stream().map(LeadDTO::getId).collect(Collectors.toList());
            todos = leadService.findBy(leadIds, status, types);
            todos = todos
                    .stream()
                    .peek(i -> mapAccountData(leads, i))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(todos);
    }

    private void mapAccountData(List<LeadDTO> leads, LeadTodoDTO todo) {
        Optional<LeadDTO> lead = leads.stream().filter(l -> l.getId().equals(todo.getLead().getLeadId())).findAny();
        lead.ifPresent(l -> {
            AccountFieldDTO account = l.getAccount();
            todo.setAccount(account);
            if (todo.getAssigneeId() != null) {
                Optional<UserAuthDTO> assignee = account.getUsers()
                        .stream()
                        .filter(u -> u.getId().equals(todo.getAssigneeId())).findAny();
                assignee.ifPresent(a -> todo.setAssignee(UserCommon.builder()
                        .fullName(a.getFullName())
                        .id(a.getId())
                        .type(UserCommonType.USER)
                        .build()));
            }
        });
    }
}
