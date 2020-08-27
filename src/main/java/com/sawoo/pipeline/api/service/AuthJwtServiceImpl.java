package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.auth.AuthenticationDTO;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequest;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.Authentication;
import com.sawoo.pipeline.api.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthJwtServiceImpl implements AuthJwtService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private final JMapper<AuthenticationDTO, Authentication> mapperDomainToDTO = new JMapper<>(AuthenticationDTO.class, Authentication.class);

    @Override
    public AuthenticationDTO create(AuthJwtRegisterRequest authJwtRegisterRequest, String identifier) throws AuthException {
        log.debug("Creating authorization component for user with identifier {}. Full name: {}. Role: {}. Provider type: {}",
                identifier,
                authJwtRegisterRequest.getFullName(),
                authJwtRegisterRequest.getRole(),
                authJwtRegisterRequest.getProviderType());

        authRepository
                .findByIdentifier(identifier)
                .ifPresent(
                        (authentication) -> {
                            throw new AuthException(
                                    ExceptionMessageConstants.AUTH_REGISTER_IDENTIFIER_ALREADY_EXISTS_EXCEPTION,
                                    new Object[]{identifier});
                        });
        Authentication authentication = newAuthentication(authJwtRegisterRequest, identifier);
        authRepository.save(authentication);
        log.debug("Authentication entity has been successfully created. Authentication identifier: {}", identifier);

        try {
            createUser(authentication, authJwtRegisterRequest);
            log.debug("User entity has been successfully created. User identifier: {}", identifier);
        } catch (UserException exc) {
            log.error("User entity could not be created for user identifier: {}. Exception: {}", identifier, exc);
            log.error("Authentication operation 'create' is going to be rolled back. Authentication component id: {} will be deleted", identifier);
            authRepository.delete(authentication);
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_REGISTER_USER_SERVICE_ERROR_EXCEPTION,
                    new String[]{identifier, exc.getMessage()});
        }

        return mapperDomainToDTO.getDestination(authentication);
    }

    @Override
    public AuthenticationDTO findById(String id) throws ResourceNotFoundException {
        log.debug("Retrieve authentication component by id: [{}]", id);

        return authRepository
                .findById(id)
                .map((authentication) -> {
                    log.debug(
                            "Authentication with id: [{}] and identifier: [{}] has been found",
                            id,
                            authentication.getIdentifier());
                    return mapperDomainToDTO.getDestination(authentication);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Authentication", id}));
    }

    @Override
    public AuthenticationDTO findByIdentifier(String identifier) throws ResourceNotFoundException {
        log.debug("Retrieve authentication component by identifier: [{}]", identifier);

        return authRepository
                .findByIdentifier(identifier)
                .map((authentication) -> {
                    log.debug(
                            "Authentication with id: [{}] and identifier: [{}] has been found",
                            authentication.getId(),
                            identifier);
                    return mapperDomainToDTO.getDestination(authentication);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Authentication", identifier}));
    }

    @Override
    public AuthenticationDTO delete(String id) throws ResourceNotFoundException {
        log.info("Deleting authentication component by id: {}", id);
        return authRepository
                .findById(id)
                .map((authentication) -> {
                    log.debug(
                            "Authentication with id: {} and identifier: {} has been found",
                            id,
                            authentication.getIdentifier());

                    authRepository.delete(authentication);
                    log.info(
                            "Authentication with id: {} and identifier: {} has been deleted",
                            id,
                            authentication.getIdentifier());
                    return mapperDomainToDTO.getDestination(authentication);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Authentication", id}));
    }

    @Override
    public List<AuthenticationDTO> findAll() {
        log.debug("Retrieving all authentication entities");
        List<AuthenticationDTO> authenticationList = StreamSupport
                .stream( authRepository.findAll().spliterator(), false)
                .map(mapperDomainToDTO::getDestination)
                .collect(Collectors.toList());
        log.debug("{} authentication entities has been found", authenticationList.size());
        return authenticationList;
    }

    @Override
    public AuthenticationDTO updatePassword(String id, String password) throws ResourceNotFoundException, AuthException {
        log.debug("Updating password for authentication component with id: {}", id);
        return authRepository
                .findById(id)
                .map( (auth) -> {
                    auth.setPassword(passwordEncoder.encode(password));
                    auth.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    log.debug("Password updated for authentication component with id {}", id);

                    return mapperDomainToDTO.getDestination(auth);
                })
                .orElseThrow( () -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ "Authentication", id}));
    }

    @Override
    public AuthenticationDTO updateIdentifier(String id, String identifier) throws ResourceNotFoundException {
        log.debug("Updating identifier for authentication component with id: {}", id);
        return authRepository
                .findById(id)
                .map( (auth) -> {
                    auth.setId(id);
                    auth.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    log.debug("Identifier updated for authentication component with id {}", id);

                    return mapperDomainToDTO.getDestination(auth);
                })
                .orElseThrow( () -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ "Authentication", id}));
    }

    private void createUser(Authentication authentication, AuthJwtRegisterRequest authJwtRegisterRequest) throws UserException {
        userService.create(newUserDTO(authentication, authJwtRegisterRequest));
    }

    private UserDTO newUserDTO(Authentication authentication, AuthJwtRegisterRequest registerRequest) {
        UserDTO user = new UserDTO();

        user.setActive(true);
        user.setId(authentication.getId());
        user.setFullName(registerRequest.getFullName());
        if (registerRequest.getRole() != null) user.getRoles().add(registerRequest.getRole());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        user.setCreated(now);
        user.setUpdated(now);

        return user;
    }

    private Authentication newAuthentication(AuthJwtRegisterRequest registerRequest, String identifier) {
        Authentication authentication = new Authentication();
        authentication.setIdentifier(identifier);
        authentication.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        authentication.setSignedUp(now);
        authentication.setUpdated(now);
        authentication.setId(UUID.randomUUID().toString());
        authentication.setProviderType(
                registerRequest.getProviderType() != null ?
                        registerRequest.getProviderType() :
                        DomainConstants.AUTHORIZATION_PROVIDER_TYPE_EMAIL);
        return authentication;
    }
}
