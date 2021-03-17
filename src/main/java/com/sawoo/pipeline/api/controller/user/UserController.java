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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.USER_CONTROLLER_API_BASE_URI)
public class UserController {

    private final UserControllerDelegator delegator;

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> create(@RequestBody UserAuthDTO dto) {
        return delegator.create(dto);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> getAll() {
        return delegator.findAll();
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @RequestMapping(
            value = "/{id}/versions",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<UserAuthDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody UserAuthUpdateDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthJwtTokenResponse> login(@RequestBody UserAuthLogin authRequest) {
        return delegator.login(authRequest);
    }

    @RequestMapping(
            value = "/reset-password",
            method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestParam("email") String userEmail) {
        return delegator.resetPassword(userEmail);
    }

    @RequestMapping(
            value = {"/confirm-reset-password", "/account-password-activation"},
            method = RequestMethod.POST)
    public ResponseEntity<Void> confirmResetPassword(@RequestBody UserAuthResetPasswordRequest resetPassword) {
        return delegator.confirmResetPassword(resetPassword);
    }

    @RequestMapping(
            value = "/is-token-valid",
            method = RequestMethod.POST)
    public ResponseEntity<Boolean> isValidToken(
            @RequestParam("token") String token) {
        return delegator.isTokenValid(token);
    }

    @RequestMapping(
            value = "/logout/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> logout(
            @PathVariable("id") String id) {
        return delegator.logout(id);
    }

    @RequestMapping(
            value = "/role",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> findByRole(
            @RequestParam(name = "roles") String[] roles) throws RestException {
        return delegator.findByRole(roles);
    }
}
