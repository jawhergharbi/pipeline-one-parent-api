package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterReq;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.mongo.UserRepositoryMongo;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthJwtServiceImpl implements UserAuthJwtService {

    private final UserRepositoryMongo repository;
    private final PasswordEncoder passwordEncoder;
    private final CommonServiceMapper mapper;

    @Override
    public UserAuthDTO create(AuthJwtRegisterReq registerRequest) throws AuthException {
        log.debug("Creating authorization component for user with [email: {}, fullName: {}, role: {}].",
                registerRequest.getEmail(),
                registerRequest.getFullName(),
                registerRequest.getRole());

        repository
                .findByEmail(registerRequest.getEmail())
                .ifPresent((auth) -> {
                            throw new AuthException(
                                    ExceptionMessageConstants.AUTH_REGISTER_IDENTIFIER_ALREADY_EXISTS_EXCEPTION,
                                    new Object[]{ registerRequest.getEmail()} );
                        });

        UserMongoDB user = newUser(registerRequest);
        user = repository.insert(user);
        log.debug("User entity has been successfully created. User identifier: {}", user.getEmail());

        return mapper.getUserAuthDomainToDTOMapper().getDestination(user);
    }

    @Override
    public UserAuthDTO delete(String id) throws ResourceNotFoundException {
        log.info("Deleting user component by [id: {}]", id);
        return repository
                .findById(id)
                .map((user) -> {
                    log.debug("User [id: {}, email: {}] has been found", id, user.getEmail());

                    repository.deleteById(id);
                    log.info("User [id: {}, email: {}] has been deleted", id, user.getEmail());
                    return mapper.getUserAuthDomainToDTOMapper().getDestination(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"User", id}));
    }

    @Override
    public UserAuthDTO findById(String id) throws ResourceNotFoundException {
        log.info("Retrieving user component by [id: {}]", id);

        return repository
                .findById(id)
                .map((user) -> {
                    log.debug("User [id: {}, email: {}] has been found", id, user.getEmail());
                    return mapper.getUserAuthDomainToDTOMapper().getDestination(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"User", id}));
    }

    @Override
    public List<UserAuthDTO> findAll() {
        log.debug("Retrieving all users entities");
        List<UserAuthDTO> users = repository
                .findAll()
                .stream()
                .map(mapper.getUserAuthDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("{} user has been found", users.size());
        return users;
    }

    @Override
    public UserAuthDTO update(UserAuthUpdateDTO userToUpdate) throws ResourceNotFoundException, AuthException {
        log.debug("Updating user  with id: [{}]", userToUpdate.getId());

        return repository
                .findById(userToUpdate.getId())
                .map((user) -> {
                    // Password
                    if (userToUpdate.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
                    }

                    // Roles
                    if (userToUpdate.getRoles() != null) {
                        user.getRoles().clear();
                        user.getRoles().add(Role.USER.name());
                    }
                    user = mapper.getUserAuthDTOToDomainMapper()
                            .getDestination(
                                    user,
                                    userToUpdate,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    user.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(user);

                    log.debug("User entity with id [{}] has been successfully updated. Updated data: {}",
                            user.getId(),
                            userToUpdate);

                    return mapper.getUserAuthDomainToDTOMapper().getDestination(user);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"User", userToUpdate.getId()}));
    }

    @Override
    public List<UserAuthDTO> findAllByRole(List<String> roles) {
        log.debug("Retrieving all users with roles [{}]", roles);
        List<UserAuthDTO> users = repository
                .findByActiveTrueAndRolesIn(roles)
                .stream()
                .map(mapper.getUserAuthDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] user/s with role/s [{}] has/have been found", users.size(), roles);
        return users;
    }

    private UserMongoDB newUser(AuthJwtRegisterReq registerRequest) {
        UserMongoDB user = new UserMongoDB();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setActive(true);
        user.setFullName(registerRequest.getFullName());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        user.setCreated(now);
        user.setUpdated(now);
        if (registerRequest.getRole() != null) {
            user.setRoles(new HashSet<>(Collections.singletonList(registerRequest.getRole())));
        } else {
            user.setRoles(new HashSet<>(Collections.singletonList(Role.USER.name())));
        }
        return user;
    }
}
