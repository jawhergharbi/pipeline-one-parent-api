package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.campaign.Campaign;
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
public class CampaignAccountServiceDecorator implements CampaignAccountService {

    private final AccountRepository accountRepository;
    private final CampaignService campaignService;
    private final AccountMapper accountMapper;

    @Autowired
    public CampaignAccountServiceDecorator(@Lazy CampaignService campaignService,
                                           AccountRepository accountRepository,
                                           AccountMapper accountMapper) {
        this.campaignService = campaignService;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public List<CampaignDTO> findByAccountIds(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) Set<String> accountIds)
            throws CommonServiceException {
        log.debug("Retrieve the list of campaigns for accounts [ids: {}]", accountIds);
        List<Campaign> campaigns = campaignService.getRepository().findByComponentIdIn(accountIds);

        log.debug("[{}] campaign/s has/have been found for account [ids: {}]", campaigns.size(), accountIds);

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

        return campaigns.stream().map(s -> {
            CampaignDTO campaign = campaignService.getMapper().getMapperOut().getDestination(s);
            Optional<Account> account = accountList.stream().filter(a -> a.getId().equals(campaign.getComponentId())).findFirst();
            account.ifPresentOrElse(
                    a -> campaign.setAccount(accountMapper.getMapperOut().getDestination(a)),
                    () -> log.warn("Campaign id [{}] where component with id [{}] has not been found",
                            campaign.getId(),
                            campaign.getComponentId()));
            return campaign;
        }).collect(Collectors.toList());
    }
}
