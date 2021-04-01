package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.lead.LeadTodoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface AccountControllerTodoDelegator {

    ResponseEntity<List<LeadTodoDTO>> findAllTODOs(List<String> accountIds, List<Integer> status, List<Integer> types)
            throws CommonServiceException;
}
