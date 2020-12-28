package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class AccountControllerDelegator extends BaseControllerDelegator<AccountDTO, AccountService>
        implements AccountControllerUserDelegator, AccountControllerLeadDelegator {

    private final AccountControllerUserDelegator userDelegator;
    private final AccountControllerLeadDelegator leadDelegator;

    @Autowired
    public AccountControllerDelegator(
            AccountService service,
            @Qualifier("accountControllerUser") AccountControllerUserDelegator userDelegator,
            @Qualifier("accountControllerLead") AccountControllerLeadDelegator leadDelegator) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
        this.userDelegator = userDelegator;
        this.leadDelegator = leadDelegator;
    }

    @Override
    public String getComponentId(AccountDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<List<AccountDTO>> findByUserId(String userId) {
        return userDelegator.findByUserId(userId);
    }

    @Override
    public ResponseEntity<?> updateUser(
            String id,
            String userId) {
        return userDelegator.updateUser(id, userId);
    }

    @Override
    public ResponseEntity<LeadDTO> createLead(String accountId, LeadDTO lead)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.createLead(accountId, lead);
    }

    @Override
    public ResponseEntity<List<LeadDTO>> findAllLeads(String accountId) throws ResourceNotFoundException {
        return leadDelegator.findAllLeads(accountId);
    }

    @Override
    public ResponseEntity<List<LeadDTO>> findAllLeads(
            String[] accountIds, Integer[] leadStatus) throws ResourceNotFoundException {
        return leadDelegator.findAllLeads(accountIds, leadStatus);
    }

    @Override
    public ResponseEntity<LeadDTO> removeLead(String accountId, String leadId) throws ResourceNotFoundException {
        return leadDelegator.removeLead(accountId, leadId);
    }
}
