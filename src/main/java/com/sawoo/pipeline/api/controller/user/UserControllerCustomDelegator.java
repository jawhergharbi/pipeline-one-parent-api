package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.RestException;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.user.UserAuthLogin;
import com.sawoo.pipeline.api.dto.user.UserAuthResetPasswordRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface UserControllerCustomDelegator {

    ResponseEntity<UserAuthJwtTokenResponse> login(@Valid UserAuthLogin authRequest) throws AuthException;

    ResponseEntity<Void>  logout(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id);

    ResponseEntity<Void>  resetPassword(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
            @Email(message = ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR)
                    String userEmail) throws AuthException;

    ResponseEntity<Void>  confirmResetPassword(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid UserAuthResetPasswordRequest resetPassword) throws AuthException;

    ResponseEntity<Boolean> isTokenValid(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
            String token);

    ResponseEntity<List<UserAuthDTO>> findByRole(@NotNull String[] roles) throws RestException;

    ResponseEntity<?> update(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            UserAuthUpdateDTO user);
}
