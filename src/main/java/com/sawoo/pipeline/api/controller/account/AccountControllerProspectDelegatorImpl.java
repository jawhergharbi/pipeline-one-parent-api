package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Qualifier("accountControllerProspect")
public class AccountControllerProspectDelegatorImpl implements AccountControllerProspectDelegator {

    private final AccountService service;

    @Autowired
    public AccountControllerProspectDelegatorImpl(AccountService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ProspectDTO> createProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @Valid ProspectDTO prospect)
            throws ResourceNotFoundException, CommonServiceException {
        ProspectDTO newEntity = service.createProspect(accountId, prospect);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI + "/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<ProspectDTO>> findAllProspects(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.findAllProspects(accountId));
    }

    @Override
    public ResponseEntity<List<ProspectDTO>> findAllProspects(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) String[] accountIds,
            Integer[] prospectQualification)
            throws ResourceNotFoundException {
        List<ProspectDTO> prospects = service.findAllProspects(accountIds, prospectQualification);
        return ResponseEntity.ok().body(prospects);
    }

    @Override
    public ResponseEntity<ProspectDTO> removeProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeProspect(accountId, prospectId));
    }
}
