package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.config.jwt.JwtTokenUtil;
import com.sawoo.pipeline.api.dto.auth.AuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.auth.login.AuthJwtLoginReq;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterReq;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.service.auth.AuthJwtUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class UserAuthJwtController {

    protected final AuthJwtUserService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserAuthDTO> create(@Valid @RequestBody AuthJwtRegisterReq registerRequest) throws AuthException {
        if (registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return Optional
                    .ofNullable( authService.create(registerRequest))
                    .map(usr -> ResponseEntity.ok().body(usr))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_REGISTER_PASSWORD_MATCH_EXCEPTION,
                    new Object[]{registerRequest.getEmail(), registerRequest.getFullName()});
        }
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<AuthJwtTokenResponse> login(@Valid @RequestBody AuthJwtLoginReq authRequest) throws AuthException {
        String email = authRequest.getEmail();
        Authentication auth = authenticate(email, authRequest.getPassword());
        UserAuthDetails user = (UserAuthDetails) auth.getPrincipal();
        try {
            final String token = jwtTokenUtil.generateToken(user, user.getId());
            return ResponseEntity.ok(new AuthJwtTokenResponse(token));
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

    private Authentication authenticate(String email, String password) throws AuthException {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_DISABLE_ERROR_EXCEPTION,
                    new String[]{ email });
        } catch (BadCredentialsException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_INVALID_CREDENTIALS_ERROR_EXCEPTION,
                    new String[]{ email });
        }
    }
}
