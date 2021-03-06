package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.RestException;
import com.sawoo.pipeline.api.config.jwt.JwtTokenUtil;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.email.EmailWithTemplateDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.user.UserAuthLogin;
import com.sawoo.pipeline.api.dto.user.UserAuthResetPasswordRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.service.infra.email.EmailService;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserControllerDelegator extends BaseControllerDelegator<UserAuthDTO, UserAuthService> implements UserControllerCustomDelegator {

    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;

    @Value("${app.auth.password-token.expiration:180}")
    private int resetPasswordExpirationTime;

    @Value("${app.auth.password-token.template-name:password-reset-email}")
    private String passwordResetTemplate;

    @Value("${app.auth.password-token.confirmation-url-key:confirm-url}")
    private String passwordResetConfirmationUrlKey;

    @Value("${app.auth.password-token.confirmation-url:auth/confirm-reset-password}")
    private String passwordResetConfirmationUrl;

    @Value("${app.web-server}")
    private String webServerPath;


    @Autowired
    public UserControllerDelegator(UserAuthService service, JwtTokenUtil jwtTokenUtil, EmailService emailService) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
    }

    @Override
    public String getComponentId(UserAuthDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<UserAuthDTO> create(@Valid UserAuthDTO dto) {
        if (dto.getPassword().equals(dto.getConfirmPassword())) {
            return Optional
                    .ofNullable( getService().create(dto) )
                    .map(usr -> {
                        try {
                            return ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .location(new URI(ControllerConstants.USER_CONTROLLER_API_BASE_URI + "/" + getComponentId(usr)))
                                    .body(usr);
                        } catch (URISyntaxException exc) {
                            throw new CommonServiceException(
                                    ExceptionMessageConstants.COMMON_INTERNAL_SERVER_ERROR_EXCEPTION,
                                    new Object[]{getClass().getSimpleName(), "create", exc.getMessage()});
                        }
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_COMMON_PASSWORD_MATCH_EXCEPTION,
                    new Object[]{ dto.getEmail(), dto.getFullName()} );
        }
    }

    @Override
    public ResponseEntity<?> update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            UserAuthDTO dto) {
        throw new RestException(
                ExceptionMessageConstants.COMMON_METHOD_NOT_ALLOWED_EXCEPTION,
                new String[]{
                        "update(String id, UserAuthUpdateDTO dto)",
                        getClass().getSimpleName(),
                        "update(String id, UserAuthUpdateDTO dto)"});
    }

    @Override
    public ResponseEntity<?> update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            UserAuthUpdateDTO user) {
        user.setId(id);
        UserAuthDTO updatedUser = getService().update(user);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI(ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI + "/" + updatedUser.getId()))
                    .body(updatedUser);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<UserAuthJwtTokenResponse> login(@Valid UserAuthLogin authRequest) throws AuthException {
        String email = authRequest.getEmail();
        UserAuthDetails user = getService().authenticate(email, authRequest.getPassword());
        try {
            final String token = jwtTokenUtil.generateToken(user, user.getId());
            return ResponseEntity.ok().body(new UserAuthJwtTokenResponse(token));
        } catch (UsernameNotFoundException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USERNAME_NOT_FOUND_ERROR_EXCEPTION,
                    new String[]{ email });
        } catch (ResourceNotFoundException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_IDENTIFIER_NOT_FOUND_ERROR_EXCEPTION,
                    new String[]{ email });
        }
    }

    @Override
    public ResponseEntity<Void> logout(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id) {
        // invalidate token
        log.info("Invalidate token for user id: [{}]. TO BE IMPLEMENTED", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> resetPassword(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
            @Email(message = ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR)
                    String userEmail) throws AuthException {
        // Create the password token
        UserTokenDTO token = getService().createToken(userEmail, UserTokenType.RESET_PASSWORD, resetPasswordExpirationTime);

        // Send email
        String confirmUrl = webServerPath
                + (webServerPath.endsWith("/") ? "" : "/")
                + passwordResetConfirmationUrl
                + "?token=" + token.getToken();
        Map<String, Object> context = new HashMap<>();
        context.put(passwordResetConfirmationUrlKey, confirmUrl);
        EmailWithTemplateDTO email = EmailWithTemplateDTO.builder()
                .templateContext(context)
                .to(userEmail)
                .subject("Pipeline.one: Reset your password")
                .templateName(passwordResetTemplate)
                .build();
        emailService.sendWithTemplate(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> confirmResetPassword(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid UserAuthResetPasswordRequest resetPassword) throws AuthException {
        if (resetPassword.getPassword().equals(resetPassword.getConfirmPassword())) {
            getService().confirmResetPassword(resetPassword.getToken(), resetPassword.getPassword());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_RESET_PASSWORD_PASSWORD_MATCH_EXCEPTION,
                    new Object[]{ resetPassword.getToken() } );
        }
    }

    @Override
    public ResponseEntity<Boolean> isTokenValid(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                    String token) {
        return ResponseEntity.status(HttpStatus.OK).body(getService().isTokenValid(token));
    }

    @Override
    public ResponseEntity<List<UserAuthDTO>> findByRole(@NotNull String[] roles) throws RestException {
        if (roles.length == 0) {
            throw new RestException(
                    ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR,
                    new Object[]{ "roles", "UserAuthJwtController.findAllByRole"});
        }
        return ResponseEntity.ok().body(getService().findAllByRole(Arrays.asList(roles)));
    }
}
