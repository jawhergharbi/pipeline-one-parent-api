package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Qualifier("accountControllerTODO")
public class AccountControllerTodoDelegatorImpl implements AccountControllerTodoDelegator {

    private final ProspectService prospectService;
    private final AccountService accountService;

    @Autowired
    public AccountControllerTodoDelegatorImpl(ProspectService prospectService, AccountService accountService) {
        this.prospectService = prospectService;
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<List<ProspectTodoDTO>> findAllTODOsIn(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) List<String> accountIds,
            List<Integer> status,
            List<Integer> types) throws CommonServiceException {
        List<ProspectDTO> prospects = accountService.findAllProspects(accountIds.toArray(new String[0]), null);
        List<ProspectTodoDTO> todos = Collections.emptyList();
        if (!prospects.isEmpty()) {
            List<String> prospectIds = prospects.stream().map(ProspectDTO::getId).collect(Collectors.toList());
            todos = prospectService.findBy(prospectIds, status, types);
            todos = todos
                    .stream()
                    .peek(i -> mapAccountData(prospects, i))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(todos);
    }

    @Override
    public ResponseEntity<List<ProspectTodoDTO>> findAllTODOs(
            @NotEmpty (message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String accountId,
            List<Integer> status,
            List<Integer> types) throws CommonServiceException {
        return findAllTODOsIn(Collections.singletonList(accountId), status, types);
    }

    private void mapAccountData(List<ProspectDTO> prospects, ProspectTodoDTO todo) {
        Optional<ProspectDTO> prospect = prospects.stream().filter(l -> l.getId().equals(todo.getProspect().getProspectId())).findAny();
        prospect.ifPresent(l -> {
            AccountDTO account = l.getAccount();
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
