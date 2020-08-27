package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.dto.auth.AuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.login.AuthJwtLoginRequestNebular;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequestNebular;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdateEmailRequest;
import com.sawoo.pipeline.api.dto.auth.update.AuthJwtUpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/auth/nebular")
public class AuthJwtControllerNebular {

    private final AuthJwtControllerHelper helper;

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthJwtTokenResponse> create(@Valid @RequestBody AuthJwtRegisterRequestNebular authJwtRegisterRequest) {
        return Optional
                .ofNullable(helper.createAuthResponse(authJwtRegisterRequest, authJwtRegisterRequest.getEmail()))
                .map((authentication) -> {
                    final String token = helper.token(authJwtRegisterRequest.getEmail(), authJwtRegisterRequest.getPassword());
                    return ResponseEntity.ok(new AuthJwtTokenResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthJwtTokenResponse> token(@Valid @RequestBody AuthJwtLoginRequestNebular authenticationRequest) {
        final String token = helper.token(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        return ResponseEntity.ok(new AuthJwtTokenResponse(token));
    }

    @RequestMapping(
            value = "/logout/{identifier}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> logout(
            @PathVariable("identifier") String identifier) {
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
            @NotNull @Valid @RequestBody AuthJwtUpdateEmailRequest authRequest) {
        return helper.updateIdentifier(authRequest, authRequest.getEmail());
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
