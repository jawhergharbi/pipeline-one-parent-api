package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.auth.AuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.login.AuthJwtLoginRequestBase;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequestBase;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdateIdentifierRequest;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthJwtControllerBase {

    private final AuthJwtControllerHelper helper;

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationDTO> create(@Valid @RequestBody AuthJwtRegisterRequestBase authJwtRegisterRequest) {
        return Optional
                .ofNullable(helper.createAuthResponse(authJwtRegisterRequest, authJwtRegisterRequest.getIdentifier()))
                .map(usr -> ResponseEntity.ok().body(usr))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthJwtTokenResponse> token(@Valid @RequestBody AuthJwtLoginRequestBase authenticationRequest) {
        final String token = helper.token(authenticationRequest.getIdentifier(), authenticationRequest.getPassword());
        return ResponseEntity.ok(new AuthJwtTokenResponse(token));
    }

    @RequestMapping(
            value = "/logout/{identifier}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> logout(
            @PathVariable("identifier")
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String identifier) {
        helper.invalidateToken(identifier);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationDTO> delete(@NotBlank @PathVariable("id") String id) {
        return helper.delete(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationDTO> getById(@NotNull @PathVariable("id") String id) {
        return helper.getById(id);
    }

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AuthenticationDTO>> getAll() {
        return helper.getAll();
    }

    @RequestMapping(
            value = "/identifier",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationDTO> updateIdentifier(
            @NotNull @Valid @RequestBody AuthJwtUpdateIdentifierRequest authRequest) {
        return helper.updateIdentifier(authRequest, authRequest.getIdentifier());
    }

    @RequestMapping(
            value = "/password",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationDTO> updatePassword(
            @NotNull @Valid @RequestBody AuthJwtUpdatePasswordRequest authRequest) {
        return helper.updatePassword(authRequest);
    }
}
