package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithTemplateDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.service.account.AccountService;
import com.sawoo.pipeline.api.service.infra.email.EmailService;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class AccountControllerDelegator extends BaseControllerDelegator<AccountDTO, AccountService>
        implements AccountControllerUserDelegator, AccountControllerProspectDelegator, AccountControllerTodoDelegator, AccountControllerCustomDelegator {

    private final AccountControllerUserDelegator userDelegator;
    private final AccountControllerProspectDelegator prospectDelegator;
    private final AccountControllerTodoDelegator todoDelegator;
    private final UserAuthService userService;
    private final EmailService emailService;

    @Value("${app.auth.activation-token.active:false}")
    private boolean activationTokenActive;

    @Value("${app.auth.activation-token.expiration:180}")
    private int activationTokenExpirationTime;

    @Value("${app.auth.activation-token.template-name:password-reset-email}")
    private String activationTokenTemplate;

    @Value("${app.auth.activation-token.confirmation-url-key:confirm-url}")
    private String activationTokenConfirmationUrlKey;

    @Value("${app.auth.activation-token.confirmation-url:auth/confirm-reset-password}")
    private String activationTokenConfirmationUrl;

    @Value("${app.auth.activation-token.user-key}")
    private String activationTokenUserKey;

    @Value("${app.web-server}")
    private String webServerPath;

    @Autowired
    public AccountControllerDelegator(
            AccountService service,
            @Qualifier("accountControllerUser") AccountControllerUserDelegator userDelegator,
            @Qualifier("accountControllerProspect") AccountControllerProspectDelegator prospectDelegator,
            @Qualifier("accountControllerTODO") AccountControllerTodoDelegator todoDelegator,
            UserAuthService userService,
            EmailService emailService) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
        this.userDelegator = userDelegator;
        this.prospectDelegator = prospectDelegator;
        this.todoDelegator = todoDelegator;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public String getComponentId(AccountDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<AccountDTO> create(@Valid AccountDTO dto) {
        ResponseEntity<AccountDTO> response = super.create(dto);

        // Create token
        if (activationTokenActive) {
            UserTokenDTO token = userService.createToken(
                    dto.getEmail(),
                    UserTokenType.ACTIVATE_ACCOUNT,
                    activationTokenExpirationTime);

            // Send email
            String confirmUrl =
                    webServerPath
                    + (webServerPath.endsWith("/") ? "" : "/")
                    + activationTokenConfirmationUrl
                    + "?token=" + token.getToken()
                    + "&activation=true";
            Map<String, Object> context = new HashMap<>();
            context.put(activationTokenConfirmationUrlKey, confirmUrl);
            context.put(activationTokenUserKey, dto);
            EmailWithTemplateDTO email = EmailWithTemplateDTO.builder()
                    .templateContext(context)
                    .to(dto.getEmail())
                    .subject("Pipeline.one: Activate your account")
                    .templateName(activationTokenTemplate)
                    .build();
            emailService.sendWithTemplate(email);
        }

        return response;
    }

    @Override
    public ResponseEntity<List<AccountDTO>> findByUserId(String userId) {
        return userDelegator.findByUserId(userId);
    }

    @Override
    public ResponseEntity<?> updateUser(
            String id,
            String userId) {
        return userDelegator.updateUser(id, userId);
    }

    @Override
    public ResponseEntity<ProspectDTO> createProspect(String accountId, ProspectDTO prospect)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.createProspect(accountId, prospect);
    }

    @Override
    public ResponseEntity<List<ProspectDTO>> findAllProspects(String accountId) throws ResourceNotFoundException {
        return prospectDelegator.findAllProspects(accountId);
    }

    @Override
    public ResponseEntity<List<ProspectDTO>> findAllProspects(
            String[] accountIds, Integer[] prospectStatus) throws ResourceNotFoundException {
        return prospectDelegator.findAllProspects(accountIds, prospectStatus);
    }

    @Override
    public ResponseEntity<ProspectDTO> removeProspect(String accountId, String prospectId) throws ResourceNotFoundException {
        return prospectDelegator.removeProspect(accountId, prospectId);
    }

    @Override
    public ResponseEntity<List<ProspectTodoDTO>> findAllTODOs(
            List<String> accountIds, List<Integer> status, List<Integer> types)
            throws CommonServiceException {
        return todoDelegator.findAllTODOs(accountIds, status, types);
    }

    @Override
    public ResponseEntity<AccountDTO> deleteAccountNotes(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteAccountNotes(accountId));
    }

    @Override
    public ResponseEntity<AccountDTO> deleteAccountCompanyNotes(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteAccountCompanyNotes(accountId));
    }
}
