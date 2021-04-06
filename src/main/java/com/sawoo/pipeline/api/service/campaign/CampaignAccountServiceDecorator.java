package com.sawoo.pipeline.api.service.campaign;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
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

    @Autowired
    public CampaignAccountServiceDecorator(@Lazy CampaignService campaignService, AccountRepository accountRepository) {
        this.campaignService = campaignService;
        this.accountRepository = accountRepository;
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

        JMapper<AccountFieldDTO, Account> accountMapper = new JMapper<>(AccountFieldDTO.class, Account.class);
        return campaigns.stream().map(s -> {
            CampaignDTO campaign = campaignService.getMapper().getMapperOut().getDestination(s);
            Optional<Account> account = accountList.stream().filter(a -> a.getId().equals(campaign.getComponentId())).findFirst();
            account.ifPresentOrElse(
                    a -> campaign.setAccount(accountMapper.getDestination(a)),
                    () -> log.warn("Campaign id [{}] where component with id [{}] has not been found",
                            campaign.getId(),
                            campaign.getComponentId()));
            return campaign;
        }).collect(Collectors.toList());
    }
}
