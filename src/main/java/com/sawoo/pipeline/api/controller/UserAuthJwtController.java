package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.RestException;
import com.sawoo.pipeline.api.config.jwt.JwtTokenUtil;
import com.sawoo.pipeline.api.dto.user.UserAuthJwtTokenResponse;
import com.sawoo.pipeline.api.dto.user.UserAuthLogin;
import com.sawoo.pipeline.api.dto.user.UserAuthRegister;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class UserAuthJwtController {

    private final UserAuthJwtService service;
    private final JwtTokenUtil jwtTokenUtil;

    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserAuthDTO> create(@Valid @RequestBody UserAuthRegister registerRequest) throws AuthException {
        if (registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return Optional
                    .ofNullable( service.create(registerRequest))
                    .map(usr -> ResponseEntity.status(HttpStatus.CREATED).body(usr))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_COMMON_PASSWORD_MATCH_EXCEPTION,
                    new Object[]{registerRequest.getEmail(), registerRequest.getFullName()});
        }
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserAuthJwtTokenResponse> login(@Valid @RequestBody UserAuthLogin authRequest) throws AuthException {
        String email = authRequest.getEmail();
        UserAuthDetails user = service.authenticate(email, authRequest.getPassword());
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

    @RequestMapping(
            value = "/logout/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> logout(
            @PathVariable("id")
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id) {
        // invalidate token
        log.info("Invalidate token for user id: [{}]. TO BE IMPLEMENTED", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> getAll() {
        List<UserAuthDTO> userList = Optional
                .ofNullable(service.findAll())
                .orElse(Collections.emptyList());
        return ResponseEntity.ok().body(userList);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> getById(@NotNull @PathVariable("id") String id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthDTO> delete(@NotBlank @PathVariable("id") String id) {
        return ResponseEntity.ok().body(service.delete(id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody UserAuthUpdateDTO user,
            @NotBlank @PathVariable String id) {
        user.setId(id);
        UserAuthDTO updatedUser = service.update(user);
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI("/api/auth/" + updatedUser.getId()))
                    .body(updatedUser);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/role",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserAuthDTO>> findByRole(
            @RequestParam(name = "roles")
            @NotNull String[] roles) throws RestException {
        if (roles.length == 0) {
            throw new RestException(
                    ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR,
                    new Object[]{ "roles", "UserAuthJwtController.findAllByRole"});
        }
        return ResponseEntity.ok().body(service.findAllByRole(Arrays.asList(roles)));
    }
}
