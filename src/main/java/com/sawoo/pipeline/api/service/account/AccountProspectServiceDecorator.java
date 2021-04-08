package com.sawoo.pipeline.api.service.account;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
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
public class AccountProspectServiceDecorator implements AccountProspectService {

    private final AccountRepository repository;
    private final ProspectService prospectService;

    @Override
    public ProspectDTO createProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @Valid ProspectDTO prospect)
            throws ResourceNotFoundException {
        log.debug("Create new prospect for account id: [{}]. Person id: [{}]", accountId, prospect.getPerson().getId());

        Account account = findAccountById(accountId);
        List<Prospect> prospects = account.getProspects();

        if (prospects.stream().anyMatch(l -> l.getPerson().getLinkedInUrl().equals(prospect.getPerson().getLinkedInUrl()))) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.ACCOUNT_PROSPECT_CREATE_PROSPECT_ALREADY_ADDED_EXCEPTION,
                    new String[] {accountId, prospect.getPerson().getLinkedInUrl()});
        }

        ProspectDTO createdProspect = prospectService.create(prospect);

        prospects.add(prospectService.getMapper().getMapperIn().getDestination(createdProspect));
        account.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(account);

        return createdProspect;
    }

    @Override
    public List<ProspectDTO> findAllProspects(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        log.debug("Retrieve prospects for account id [{}]", accountId);

        Account account = findAccountById(accountId);

        log.debug("[{}] prospect/s has/have been found for account id [{}]", account.getProspects().size(), accountId);

        JMapper<AccountFieldDTO, Account> accountMapper = new JMapper<>(AccountFieldDTO.class, Account.class);
        AccountFieldDTO accountProspect = accountMapper.getDestination(account);
        return account.getProspects()
                .stream()
                .map( l -> {
                    ProspectDTO prospect = prospectService.getMapper().getMapperOut().getDestination(l);
                    prospect.setAccount(accountProspect);
                    return prospect;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProspectDTO> findAllProspects(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) String[] accountIds,
            Integer[] prospectQualification) throws ResourceNotFoundException {
        log.debug("Retrieve prospects from a list of accounts with the following ids [{}]", Arrays.toString(accountIds));

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
        Predicate<Prospect> statusFilter = (prospectQualification != null && prospectQualification.length > 0) ?
                (l -> l.getQualification() == null || Arrays.asList(prospectQualification).contains(l.getQualification().getValue())) :
                l -> true;
        List<ProspectDTO> prospects = accounts
                .stream().flatMap( account -> {
                    AccountFieldDTO prospectAccount = accountMapper.getDestination(account);
                    return account.getProspects()
                            .stream()
                            .filter(statusFilter)
                            .map(l -> {
                                ProspectDTO prospect = prospectService.getMapper().getMapperOut().getDestination(l);
                                prospect.setAccount(prospectAccount);
                                return prospect;
                            });
                }).collect(Collectors.toList());
        log.debug("[{}] prospect/s has/have been found for accounts with ids [{}] and status [{}]",
                prospects.size(),
                accountIds,
                prospectQualification);

        return prospects;
    }

    @Override
    public ProspectDTO removeProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException {
        log.debug("Remove prospect id [{}] from account id[{}]", prospectId, accountId);

        Account account = findAccountById(accountId);

        return account
                .getProspects()
                .stream()
                .filter(p -> prospectId.equals(p.getId()))
                .findAny()
                .map(l -> {
                    account.getProspects().remove(l);
                    account.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(account);
                    prospectService.delete(prospectId);
                    return prospectService.getMapper().getMapperOut().getDestination(l);
                })
                .orElseThrow( () ->
                    new CommonServiceException(
                            ExceptionMessageConstants.ACCOUNT_PROSPECT_REMOVE_PROSPECT_NOT_FOUND_EXCEPTION,
                            new String[] {accountId, prospectId}));
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
