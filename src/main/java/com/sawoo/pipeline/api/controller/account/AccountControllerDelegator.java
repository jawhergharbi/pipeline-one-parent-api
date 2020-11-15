package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AccountControllerDelegator extends BaseControllerDelegator<AccountDTO, AccountService> implements AccountControllerCustomDelegator {

    @Autowired
    public AccountControllerDelegator(AccountService service) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(AccountDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<List<AccountDTO>> findByUserId(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String userId) {
        return ResponseEntity.ok().body(getService().findAllByUser(userId));
    }

    @Override
    public ResponseEntity<?> updateUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String userId) {
        AccountDTO entityUpdated = getService().updateUser(id, userId);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI(ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI + "/" + entityUpdated.getId()))
                    .body(entityUpdated);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
