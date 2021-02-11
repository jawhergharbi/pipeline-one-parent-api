package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.CommonConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.AuthException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserAuthUpdateDTO;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@Validated
public class UserAuthServiceImpl extends BaseServiceImpl<UserAuthDTO, User, UserRepository, UserAuthMapper> implements UserAuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService tokenService;

    @Value("${app.auth.default-expiration:180}")
    private int defaultExpirationTime;

    @Autowired
    public UserAuthServiceImpl(UserRepository repository,
                               UserAuthMapper mapper,
                               UserAuthServiceEventListener eventListener,
                               AuthenticationManager authenticationManager,
                               PasswordEncoder passwordEncoder,
                               UserTokenService tokenService) {
        super(repository, mapper, DBConstants.USER_DOCUMENT, eventListener);
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> entityExists(UserAuthDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, email: {}]",
                DBConstants.USER_DOCUMENT,
                entityToCreate.getEmail());
        return getRepository().findByEmail(entityToCreate.getEmail());
    }

    @Override
    public UserAuthDTO update(UserAuthUpdateDTO userToUpdate) throws ResourceNotFoundException, AuthException {
        log.debug("Updating user with id: [{}]", userToUpdate.getId());

        if (userToUpdate.getId() == null || userToUpdate.getId().length() == 0) {
            throw new AuthException(
                    ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR,
                    new String[]{ "id", "User"});
        }

        return getRepository()
                .findById(userToUpdate.getId())
                .map((user) -> {
                    // Validation
                    userUpdateValidation(userToUpdate, user);

                    // Password
                    if (userToUpdate.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
                    }

                    // Roles
                    if (userToUpdate.getRoles() != null) {
                        user.getRoles().clear();
                        user.getRoles().add(UserRole.USER.name());
                    }
                    user = getMapper().getMapperIn()
                            .getDestination(
                                    user,
                                    userToUpdate,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    user.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    getRepository().save(user);

                    log.debug("User entity with id [{}] has been successfully updated. Updated data: {}",
                            user.getId(),
                            userToUpdate);

                    return getMapper().getMapperOut().getDestination(user);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"User", userToUpdate.getId()}));
    }

    @Override
    public UserTokenDTO createToken(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Email(message = ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR) String userEmail,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) UserTokenType type,
            int expirationTime)
            throws AuthException {
        log.debug("Create a token of the type [{}] for user with [{}]", type.name(), userEmail);
        User user = getRepository()
                .findByEmail(userEmail)
                .orElseThrow(() -> new AuthException(
                        ExceptionMessageConstants.AUTH_TOKEN_EMAIL_NOT_FOUND_ERROR_EXCEPTION,
                        new String[]{type.name(), userEmail}));
        // Get user token
        UserTokenDTO token = getToken(user, type, expirationTime < 0 ? defaultExpirationTime : expirationTime);

        log.debug("Token [{}] of type [{}] created for user id [{}]", token.getToken(), type, user.getId());

        return token;
    }

    @Override
    public UserAuthDTO confirmResetPassword(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String token,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String newPassword)
            throws AuthException {
        log.debug("Confirming reset password for user with reset token [{}]", token);

        Optional<UserTokenDTO> tokenEntity = tokenService.findByToken(token);

        return tokenEntity.map((ut) -> {
            if (LocalDateTime.now(ZoneOffset.UTC).isAfter(ut.getExpirationDate())) {
                throw new AuthException(
                        ExceptionMessageConstants.AUTH_RESET_PASSWORD_CONFIRM_TOKEN_EXPIRED_ERROR_EXCEPTION,
                        new String[]{token, ut.getExpirationDate().toString()});
            } else {
                // update user password
                UserAuthUpdateDTO updateUser = new UserAuthUpdateDTO();
                updateUser.setPassword(newPassword);
                updateUser.setConfirmPassword(newPassword);
                updateUser.setId(tokenEntity.get().getUserId());

                UserAuthDTO user = update(updateUser);
                log.debug("Password successfully reset for user [id: {}, email: {}]", user.getId(), user.getEmail());

                // delete token
                tokenService.delete(ut.getId());

                return user;
            }
        }).orElseThrow(() -> new AuthException(
                ExceptionMessageConstants.AUTH_RESET_PASSWORD_CONFIRM_TOKEN_NOT_FOUND_ERROR_EXCEPTION,
                new String[]{token}));
    }

    @Override
    public List<UserAuthDTO> findAllByRole(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Size(min = 1, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR) List<String> roles) {
        log.debug("Retrieving all users with roles [{}]", roles);

        List<UserAuthDTO> users = getRepository()
                .findByActiveTrueAndRolesIn(roles)
                .stream()
                .map(getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] user/s with role/s [{}] has/have been found", users.size(), roles);
        return users;
    }

    @Override
    public UserAuthDetails authenticate(String email, String password) throws AuthException {
        log.debug("Authenticating user with email: [{}]", email);
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return (UserAuthDetails) auth.getPrincipal();
        } catch (DisabledException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_DISABLE_ERROR_EXCEPTION,
                    new String[]{email});
        } catch (ClassCastException exc) {
            String[] params = new String[]{
                    UserAuthDetails.class.getName(),
                    (auth != null && auth.getPrincipal() != null) ? auth.getPrincipal().getClass().getName() : "null"
            };
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_USER_DETAILS_CLASS_ERROR_EXCEPTION,
                    params);
        } catch (BadCredentialsException exc) {
            throw new AuthException(
                    ExceptionMessageConstants.AUTH_LOGIN_INVALID_CREDENTIALS_ERROR_EXCEPTION,
                    new String[]{ email });
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        Optional<UserTokenDTO> tokenEntity = tokenService.findByToken(token);
        return tokenEntity.map((ut) -> {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime expirationDateTime = ut.getExpirationDate();
            return now.isBefore(expirationDateTime);
        }).orElse(false);
    }

    private void userUpdateValidation(UserAuthUpdateDTO userToUpdate, User user) throws AuthException {
        if (userToUpdate.getPassword() != null) {
            if (!userToUpdate.getPassword().equals(userToUpdate.getConfirmPassword())) {
                throw new AuthException(
                        ExceptionMessageConstants.AUTH_COMMON_PASSWORD_MATCH_EXCEPTION,
                        new String[]{ user.getEmail(), user.getFullName()});
            }
            if (userToUpdate.getPassword().length() < CommonConstants.AUTH_PASSWORD_MIN_LENGTH) {
                throw new AuthException(
                        ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR,
                        new Object[]{ "password", CommonConstants.AUTH_PASSWORD_MIN_LENGTH });
            }
        }
    }

    private UserTokenDTO getToken(User user, UserTokenType type, int expirationTime) {
        List<UserTokenDTO> tokenList = tokenService.findAllByUserIdAndType(user.getId(), type);
        Optional<UserTokenDTO> tokenExists = tokenList
                .stream()
                .filter(ut -> ut.getExpirationDate().isAfter(LocalDateTime.now(ZoneOffset.UTC)))
                .findFirst();

        return tokenExists.orElseGet(() -> {
            UserTokenDTO token = UserTokenDTO.builder()
                    .token(UUID.randomUUID().toString())
                    .type(type)
                    .userId(user.getId())
                    .expirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(expirationTime))
                    .build();
            return tokenService.create(token);
        });
    }
}
