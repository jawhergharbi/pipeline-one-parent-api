package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.account.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class SequenceAccountServiceDecorator implements SequenceAccountService {

    private final AccountRepository accountRepository;
    private final SequenceService sequenceService;
    private final AccountMapper accountMappert;

    @Autowired
    public SequenceAccountServiceDecorator(@Lazy SequenceService sequenceService,
                                           AccountRepository accountRepository,
                                           AccountMapper accountMapper) {
        this.sequenceService = sequenceService;
        this.accountRepository = accountRepository;
        this.accountMappert = accountMapper;
    }

    @Override
    public List<SequenceDTO> findByAccountIds(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) Set<String> accountIds)
            throws CommonServiceException {
        log.debug("Retrieve the list of sequences for accounts [ids: {}]", accountIds);
        List<Sequence> sequences = sequenceService.getRepository().findByComponentIdIn(accountIds);

        log.debug("[{}] sequence/s has/have been found for account [ids: {}]", sequences.size(), accountIds);

        Iterable<Account> accounts = accountRepository.findAllById(accountIds);
        List<Account> accountList = StreamSupport
                .stream(accounts.spliterator(), false)
                .collect(Collectors.toList());
        if (accountList.size() < accountIds.spliterator().getExactSizeIfKnown()) {
            log.warn(
                    "[{}] account/s found for the following account ids [{}]. Number of account found does not match the accounts requested",
                    accounts.spliterator().getExactSizeIfKnown(),
                    accountIds);
        }

        return sequences.stream().map(s -> {
            SequenceDTO sequence = sequenceService.getMapper().getMapperOut().getDestination(s);
            Optional<SequenceUserDTO> owner = sequence.getUsers().stream().filter(su -> SequenceUserType.OWNER.equals(su.getType())).findFirst();
            owner.ifPresent(o -> sequence.setOwnerId(o.getUserId()));
            Optional<Account> account = accountList.stream().filter(a -> a.getId().equals(sequence.getComponentId())).findFirst();
            account.ifPresentOrElse(
                    a -> sequence.setAccount(accountMappert.getMapperOut().getDestination(a)),
                    () -> log.warn("Sequence id [{}] where component with id [{}] has not been found",
                            sequence.getId(),
                            sequence.getComponentId()));
            return sequence;
        }).collect(Collectors.toList());
    }
}
