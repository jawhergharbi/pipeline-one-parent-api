package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.lead.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier(value = "accountLeadService")
@RequiredArgsConstructor
public class AccountLeadServiceDecorator implements AccountLeadService {

    private final AccountRepository repository;
    private final LeadService leadService;

    @Override
    public LeadDTO createLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @Valid LeadDTO lead)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Creating new lead for account id: [{}]. Prospect id: [{}]", accountId, lead.getProspect().getId());

        Account account = findAccountById(accountId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LeadDTO createdLead = leadService.create(lead);

        account.getLeads().add(leadService.getMapper().getMapperIn().getDestination(createdLead));
        account.setUpdated(now);
        repository.save(account);

        return createdLead;
    }

    @Override
    public List<LeadDTO> findAllLeads(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        log.debug("Retrieving leads for account id [{}]", accountId);

        Account account = findAccountById(accountId);

        log.debug("[{}] lead/s has/have been found for account id [{}]", account.getLeads().size(), accountId);

        return account.getLeads()
                .stream()
                .map(leadService.getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadDTO> findAllLeads(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) String[] accountIds,
            Integer[] leadStatus) throws ResourceNotFoundException {
        log.debug("Retrieving leads for a list of accounts with the following ids [{}]", Arrays.toString(accountIds));
        return null;
    }

    @Override
    public LeadDTO removeLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Remove lead id [{}] from account id[{}]", leadId, accountId);

        Account account = findAccountById(accountId);

        return account
                .getLeads()
                .stream()
                .filter(lead -> leadId.equals(lead.getId()))
                .findAny()
                .map((l) -> {
                    account.getLeads().remove(l);
                    account.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(account);
                    leadService.delete(leadId);
                    return leadService.getMapper().getMapperOut().getDestination(l);
                })
                .orElseThrow( () ->
                    new CommonServiceException(
                            ExceptionMessageConstants.ACCOUNT_LEAD_REMOVE_LEAD_NOT_FOUND_EXCEPTION,
                            new String[] {accountId, leadId}));
    }

    private Account findAccountById(String accountId) throws ResourceNotFoundException {
        return repository
                .findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.ACCOUNT_DOCUMENT, accountId }));
    }
}
