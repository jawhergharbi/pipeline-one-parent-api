package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.account.AccountMapper;
import com.sawoo.pipeline.api.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CampaignAccountServiceDecorator implements CampaignAccountService {

    private final CampaignService campaignService;
    private final AccountService accountService;

    @Autowired
    public CampaignAccountServiceDecorator(@Lazy CampaignService campaignService,
                                           AccountRepository accountRepository,
                                           AccountService accountService,
                                           AccountMapper accountMapper) {
        this.campaignService = campaignService;
        this.accountService = accountService;
    }

    @Override
    public List<CampaignDTO> findByAccountIds(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) Set<String> accountIds)
            throws CommonServiceException {
        log.debug("Retrieve the list of campaigns for accounts [ids: {}]", accountIds);
        List<Campaign> campaigns = campaignService.getRepository().findByComponentIdInShort(accountIds);

        log.debug("[{}] campaign/s has/have been found for account [ids: {}]", campaigns.size(), accountIds);

        List<AccountDTO> accountList = accountService.findAllById(accountIds);

        return campaigns.stream().map(s -> {
            CampaignDTO campaign = campaignService.getMapper().getMapperOut().getDestination(s);
            Optional<AccountDTO> account = accountList.stream().filter(a -> a.getId().equals(campaign.getComponentId())).findFirst();
            account.ifPresentOrElse(
                    campaign::setAccount,
                    () -> log.warn("Campaign id [{}] where component with id [{}] has not been found",
                            campaign.getId(),
                            campaign.getComponentId()));
            return campaign;
        }).collect(Collectors.toList());
    }
}
