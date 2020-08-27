package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.config.jwt.JwtTokenUtil;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdatePasswordRequest;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdateRequest;
import com.sawoo.pipeline.api.service.AuthJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthJwtControllerHelper {

    protected final AuthJwtService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    protected AuthenticationDTO createAuthResponse(AuthJwtRegisterRequest authJwtRegisterRequest, String identifier) throws AuthException {
        if (authJwtRegisterRequest.getPassword().equals(authJwtRegisterRequest.getConfirmPassword())) {
            return authService.create(authJwtRegisterRequest, identifier);
        } else {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_REGISTER_PASSWORD_MATCH_EXCEPTION,
                    new Object[]{identifier, authJwtRegisterRequest.getFullName()});
        }
    }

    protected String token(String authId, String password) throws AuthException {
        authenticate(authId, password);
        try {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authId);
            AuthenticationDTO auth = authService.findByIdentifier(authId);
            return jwtTokenUtil.generateToken(userDetails, auth.getId());
        } catch (UsernameNotFoundException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USERNAME_NOT_FOUND_ERROR_EXCEPTION,
                    new String[]{authId});
        } catch (ResourceNotFoundException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_IDENTIFIER_NOT_FOUND_ERROR_EXCEPTION,
                    new String[]{authId});
        }
    }

    protected void invalidateToken(String authId) throws AuthException {
        log.debug("Invalidate token for user identifier: [{}]", authId);
    }

    private void authenticate(String username, String password) throws AuthException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_DISABLE_ERROR_EXCEPTION,
                    new String[]{username});
        } catch (BadCredentialsException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_INVALID_CREDENTIALS_ERROR_EXCEPTION,
                    new String[]{username});
        }
    }

    protected ResponseEntity<AuthenticationDTO> delete(String authId) {
        return ResponseEntity.ok().body(authService.delete(authId));
    }

    protected ResponseEntity<AuthenticationDTO> getById(String authId) {
        return ResponseEntity.ok().body(authService.findById(authId));
    }

    protected ResponseEntity<List<AuthenticationDTO>> getAll() {
        List<AuthenticationDTO> authenticationList = Optional
                .ofNullable(authService.findAll())
                .orElse(Collections.emptyList());
        return ResponseEntity.ok().body(authenticationList);
    }

    protected ResponseEntity<AuthenticationDTO> updateIdentifier(AuthJwtUpdateRequest authRequest, String identifier) {
        return ResponseEntity.ok().body(authService.updateIdentifier(authRequest.getId(), identifier));
    }

    protected ResponseEntity<AuthenticationDTO> updatePassword(AuthJwtUpdatePasswordRequest authRequest) {
        return ResponseEntity.ok().body(authService.updatePassword(authRequest.getId(), authRequest.getPassword()));
    }
}
