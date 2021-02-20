package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("sequenceAccountController")
public class SequenceControllerAccountDelegatorImpl implements SequenceControllerAccountDelegator {

    private final SequenceService service;

    @Autowired
    public SequenceControllerAccountDelegatorImpl(SequenceService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<SequenceDTO>> findByAccounts(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) Set<String> accountIds)
            throws CommonServiceException {
        return ResponseEntity.ok().body(service.findByAccountIds(accountIds));
    }
}
