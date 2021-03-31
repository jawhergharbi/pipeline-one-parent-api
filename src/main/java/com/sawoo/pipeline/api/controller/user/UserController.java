package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.common.exceptions.RestException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.user.UserAuthLogin;
import com.sawoo.pipeline.api.dto.user.UserAuthResetPasswordRequest;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.USER_CONTROLLER_API_BASE_URI)
public class UserController {

    private final UserControllerDelegator delegator;

    @PostMapping(
            value = "/register",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> create(@RequestBody UserAuthDTO dto) {
        return delegator.create(dto);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> getAll() {
        return delegator.findAll();
    }

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<UserAuthDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody UserAuthUpdateDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @PostMapping(
            value = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthJwtTokenResponse> login(@RequestBody UserAuthLogin authRequest) {
        return delegator.login(authRequest);
    }

    @PostMapping(
            value = "/reset-password",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetPassword(@RequestParam("email") String userEmail) {
        return delegator.resetPassword(userEmail);
    }

    @PostMapping(
            value = {"/confirm-reset-password", "/account-password-activation"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> confirmResetPassword(@RequestBody UserAuthResetPasswordRequest resetPassword) {
        return delegator.confirmResetPassword(resetPassword);
    }

    @PostMapping(value = "/is-token-valid")
    public ResponseEntity<Boolean> isValidToken(
            @RequestParam("token") String token) {
        return delegator.isTokenValid(token);
    }

    @DeleteMapping(value = "/logout/{id}")
    public ResponseEntity<Void> logout(
            @PathVariable("id") String id) {
        return delegator.logout(id);
    }

    @GetMapping(
            value = "/role",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> findByRole(
            @RequestParam(name = "roles") String[] roles) throws RestException {
        return delegator.findByRole(roles);
    }
}
