package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface AccountControllerInteractionDelegator {

    ResponseEntity<List<InteractionDTO>> findAllInteractions(String[] accountIds, Integer[] status, Integer[] types)
            throws CommonServiceException;
}
