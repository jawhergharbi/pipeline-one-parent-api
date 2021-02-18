package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("sequenceUserController")
public class SequenceControllerUserDelegatorImpl implements SequenceControllerUserDelegator {

    private final SequenceService service;

    @Autowired
    public SequenceControllerUserDelegatorImpl(SequenceService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<SequenceDTO> deleteUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                    String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                    String userId) throws ResourceNotFoundException, CommonServiceException {
        SequenceDTO entityUpdated = service.deleteUser(id, userId);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI(ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI + "/" + entityUpdated.getId()))
                    .body(entityUpdated);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<SequenceDTO>> findByAccounts(Set<String> accountIds) throws CommonServiceException {
        return ResponseEntity.ok().body(service.findByAccountIds(accountIds));
    }
}
