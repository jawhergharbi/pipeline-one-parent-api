package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.lead.LeadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier(value = "accountLeadService")
@RequiredArgsConstructor
public class AccountLeadServiceDecorator implements AccountLeadService {

    private final AccountRepository repository;
    private final LeadMapper leadMapper;

    @Override
    public LeadDTO createLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @Valid LeadDTO lead)
            throws ResourceNotFoundException, CommonServiceException {
        return null;
    }

    @Override
    public List<LeadDTO> findAllLeads(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        log.debug("Retrieving leads for account id [{}]", accountId);

        Account account = repository
                .findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ "Account", accountId }));

        log.debug("[{}] lead/s has/have been found for account id [{}]", account.getLeads().size(), accountId);

        return account.getLeads()
                .stream()
                .map(leadMapper.getMapperOut()::getDestination)
                .collect(Collectors.toList());
    }

    @Override
    public LeadDTO removeLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        return null;
    }
}
