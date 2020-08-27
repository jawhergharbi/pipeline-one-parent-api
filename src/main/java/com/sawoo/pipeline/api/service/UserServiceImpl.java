package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.repository.UserRepository;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommonServiceMapper mapper;

    @Override
    public UserDTO create(UserDTO userDTO) throws UserException {
        log.debug("Creating new user. id: {}", userDTO.getId());

        userRepository
                .findById(userDTO.getId())
                .ifPresent((user) -> {
                    throw new UserException(
                            ExceptionMessageConstants.USER_CREATE_USER_ALREADY_EXISTS_EXCEPTION,
                            new String[]{userDTO.getId()});
                });
        User user = mapper.getUserDTOToDomainMapper().getDestination(userDTO);
        user.getRoles().add(Role.USER.name());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        user.setCreated(now);
        user.setUpdated(now);
        userRepository.save(user);

        log.debug("User has been successfully created. User: {}", user);

        return mapper.getUserDomainToDTOMapper().getDestination(user);
    }

    @Override
    public Optional<UserDTO> update(String id, UserDTO userDTO) {
        log.debug("Updating user  with id: {}", id);

        return userRepository
                .findById(id)
                .map((user) -> {
                    /* TODO: at the moment we reset 'roles' property every time that a request for update is executed
                    Later on, we may have 'roles' as a separate entity and we would add the end points to manage them */
                    if (userDTO.getRoles() != null) {
                        user.getRoles().clear();
                        user.getRoles().add(Role.USER.name());
                    }
                    user = mapper.getUserDTOToDomainMapper()
                            .getDestination(
                                    user,
                                    userDTO,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    user.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    userRepository.save(user);

                    log.debug("User entity with id {} has been successfully updated. Updated data: {}", id, userDTO);
                    return Optional.of(mapper.getUserDomainToDTOMapper().getDestination(user));
                })
                .orElseGet(() -> {
                    log.info("User entity with id: {} does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public List<UserDTO> findAll() {
        log.debug("Retrieving all users entities");
        List<UserDTO> users = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(mapper.getUserDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("{} user has been found", users.size());
        return users;
    }

    @Override
    public List<UserDTO> findAllByRoles(List<String> roles) {
        log.debug("Retrieving all users with roles [{}]", roles);
        // TODO: check if there is a more efficient way to do this
        List<UserDTO> users = roles
                .stream()
                .flatMap((role) -> userRepository.findAllByRoles(role).stream())
                .distinct()
                .map(mapper.getUserDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] user with role/s [{}] has/have been found", users.size(), roles);
        return users;
    }

    @Override
    public UserDTO findById(String id) throws ResourceNotFoundException {
        log.debug("Retrieving user by id. Id: {}", id);

        return userRepository
                .findById(id)
                .map(mapper.getUserDomainToDTOMapper()::getDestination)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"User", id}));
    }

    @Override
    public Optional<UserDTO> delete(String id) throws ResourceNotFoundException {
        log.debug("Deleting user entity with id: {}", id);

        return userRepository
                .findById(id)
                .map((user) -> {
                    userRepository.delete(user);
                    log.debug("User entity with id: {} has been deleted", id);
                    return Optional.of(mapper.getUserDomainToDTOMapper().getDestination(user));
                })
                .orElseGet(() -> {
                    log.info("User entity with id: {} does not exist", id);
                    return Optional.empty();
                });
    }
}
