package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithTemplateDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
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
        implements AccountControllerUserDelegator, AccountControllerLeadDelegator, AccountControllerInteractionDelegator, AccountControllerCustomDelegator {

    private final AccountControllerUserDelegator userDelegator;
    private final AccountControllerLeadDelegator leadDelegator;
    private final AccountControllerInteractionDelegator interactionDelegator;
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
            @Qualifier("accountControllerLead") AccountControllerLeadDelegator leadDelegator,
            @Qualifier("accountControllerInteraction") AccountControllerInteractionDelegator interactionDelegator,
            UserAuthService userService,
            EmailService emailService) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
        this.userDelegator = userDelegator;
        this.leadDelegator = leadDelegator;
        this.interactionDelegator = interactionDelegator;
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
    public ResponseEntity<LeadDTO> createLead(String accountId, LeadDTO lead)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.createLead(accountId, lead);
    }

    @Override
    public ResponseEntity<List<LeadDTO>> findAllLeads(String accountId) throws ResourceNotFoundException {
        return leadDelegator.findAllLeads(accountId);
    }

    @Override
    public ResponseEntity<List<LeadDTO>> findAllLeads(
            String[] accountIds, Integer[] leadStatus) throws ResourceNotFoundException {
        return leadDelegator.findAllLeads(accountIds, leadStatus);
    }

    @Override
    public ResponseEntity<LeadDTO> removeLead(String accountId, String leadId) throws ResourceNotFoundException {
        return leadDelegator.removeLead(accountId, leadId);
    }

    @Override
    public ResponseEntity<List<LeadInteractionDTO>> findAllInteractions(
            List<String> accountIds, List<Integer> status, List<Integer> types)
            throws CommonServiceException {
        return interactionDelegator.findAllInteractions(accountIds, status, types);
    }

    @Override
    public ResponseEntity<AccountDTO> deleteAccountNotes(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(getService().deleteAccountNotes(accountId));
    }
}
