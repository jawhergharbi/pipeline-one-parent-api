package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface AccountControllerInteractionDelegator {

    ResponseEntity<List<LeadInteractionDTO>> findAllInteractions(List<String> accountIds, List<Integer> status, List<Integer> types)
            throws CommonServiceException;
}
