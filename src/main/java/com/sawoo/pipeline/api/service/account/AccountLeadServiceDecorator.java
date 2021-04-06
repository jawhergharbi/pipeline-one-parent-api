package com.sawoo.pipeline.api.service.account;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.lead.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountLeadServiceDecorator implements AccountLeadService {

    private final AccountRepository repository;
    private final LeadService leadService;

    @Override
    public LeadDTO createLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @Valid LeadDTO lead)
            throws ResourceNotFoundException {
        log.debug("Creating new lead for account id: [{}]. Person id: [{}]", accountId, lead.getPerson().getId());

        Account account = findAccountById(accountId);
        List<Lead> leads = account.getLeads();

        if (leads.stream().anyMatch(l -> l.getPerson().getLinkedInUrl().equals(lead.getPerson().getLinkedInUrl()))) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.ACCOUNT_LEAD_CREATE_LEAD_ALREADY_ADDED_EXCEPTION,
                    new String[] {accountId, lead.getPerson().getLinkedInUrl()});
        }

        LeadDTO createdLead = leadService.create(lead);

        leads.add(leadService.getMapper().getMapperIn().getDestination(createdLead));
        account.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
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

        JMapper<AccountFieldDTO, Account> accountMapper = new JMapper<>(AccountFieldDTO.class, Account.class);
        AccountFieldDTO accountLead = accountMapper.getDestination(account);
        return account.getLeads()
                .stream()
                .map( l -> {
                    LeadDTO lead = leadService.getMapper().getMapperOut().getDestination(l);
                    lead.setAccount(accountLead);
                    return lead;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadDTO> findAllLeads(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) String[] accountIds,
            Integer[] leadStatus) throws ResourceNotFoundException {
        log.debug("Retrieving leads from a list of accounts with the following ids [{}]", Arrays.toString(accountIds));

        List<Account> accounts = StreamSupport
                .stream(repository
                        .findAllById(Arrays.stream(accountIds).collect(Collectors.toList()))
                        .spliterator(), false)
                .collect(Collectors.toList());
        if (accounts.size() < accountIds.length) {
            log.warn(
                    "[{}] account/s found for the following account ids [{}]. Number of account found does not match the accounts requested",
                    accounts.size(),
                    accountIds);
        }
        JMapper<AccountFieldDTO, Account> accountMapper = new JMapper<>(AccountFieldDTO.class, Account.class);
        Predicate<Lead> statusFilter = (leadStatus != null && leadStatus.length > 0) ?
                (l -> l.getStatus() == null || Arrays.asList(leadStatus).contains(l.getStatus().getValue())) :
                l -> true;
        List<LeadDTO> leads = accounts
                .stream().flatMap( account -> {
                    AccountFieldDTO leadAccount = accountMapper.getDestination(account);
                    return account.getLeads()
                            .stream()
                            .filter(statusFilter)
                            .map(l -> {
                                LeadDTO lead = leadService.getMapper().getMapperOut().getDestination(l);
                                lead.setAccount(leadAccount);
                                return lead;
                            });
                }).collect(Collectors.toList());
        log.debug("[{}] lead/s has/have been found for accounts with ids [{}] and status [{}]",
                leads.size(),
                accountIds,
                leadStatus);

        return leads;
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
                .map(l -> {
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
