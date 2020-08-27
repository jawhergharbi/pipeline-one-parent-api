package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserException;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest extends BaseServiceTest {

    private final JMapper<UserDTO, User> mapperDomainToDTO = new JMapper<>(UserDTO.class, User.class);
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("User Service: findById - Success")
    void findByIdWhenUserExitsReturnsSuccess() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        User mockedUserEntity = getMockFactory().newUserEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        UserDTO returnedUser = userService.findById(COMPONENT_ID);

        // Assert the response
        Assertions.assertNotNull(returnedUser, "User entity with id " + COMPONENT_ID + " was not found");
        Assertions.assertEquals(returnedUser.getId(), COMPONENT_ID, "User.id should be the same");

        verify(userRepository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("User Service: findById when user does not exists - Failure")
    void findByIdWhenUserNotFoundReturnsResourceNotFoundException() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(COMPONENT_ID);

        // Asserts
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findById(COMPONENT_ID),
                "findById must throw a ResourceNotFoundException");
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(userRepository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("User Service: create when user does not exist - Success")
    void createWhenUserDoesNotExistReturnsSuccess() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(COMPONENT_ID);
        User mockedUserEntity = getMockFactory().newUserEntity(COMPONENT_ID);
        UserDTO expectedReturnedDTO = mapperDomainToDTO.getDestination(mockedUserEntity);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(COMPONENT_ID);
        doReturn(mockedUserEntity).when(userRepository).save(any());

        // Execute the service call
        UserDTO returnedEntity = userService.create(mockedUserDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "User entity with id " + COMPONENT_ID + " already exists in the system");
        Assertions.assertEquals(expectedReturnedDTO.getId(), returnedEntity.getId(), "User.id should be the same");
        Assertions.assertEquals(true, returnedEntity.getActive(), "User.active must be true");
        Assertions.assertTrue(returnedEntity.getRoles().size() > 0, "User roles must contain any element");
        Assertions.assertTrue(returnedEntity.getRoles().contains(Role.USER.name()), String.format("User roles must contain role %s", Role.USER.name()));
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("User Service: create when user does exist - Failure")
    void createWhenUserExistsReturnsUserException() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(COMPONENT_ID);
        User mockedUserEntity = getMockFactory().newUserEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(COMPONENT_ID);

        // Asserts
        UserException exception = Assertions.assertThrows(
                UserException.class,
                () -> userService.create(mockedUserDTO),
                "create must throw a UserException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.USER_CREATE_USER_ALREADY_EXISTS_EXCEPTION,
                "Exception message must be " + ExceptionMessageConstants.USER_CREATE_USER_ALREADY_EXISTS_EXCEPTION);
        Assertions.assertEquals(
                1,
                exception.getArgs().length,
                "Number of arguments in the exception must be 1");

        verify(userRepository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("User Service: findAll - Success")
    void findAllWhenThereAreUsersReturnsSuccess() {
        // Set up mock user entities
        int listSize = 3;
        List<User> userList = IntStream.range(0, listSize)
                .mapToObj((user) -> {
                    String USER_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
                    return getMockFactory().newUserEntity(USER_ID);
                }).collect(Collectors.toList());

        // Set up the mocked repository
        doReturn(userList).when(userRepository).findAll();

        // Execute the service call
        List<UserDTO> returnedUserList = userService.findAll();

        Assertions.assertFalse(returnedUserList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedUserList.size(), String.format("Returned list size must be %d", listSize));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("User Service: findAll empty list - Success")
    void findAllWhenThereAreNoUsersReturnsSuccess() {
        // Set up mock user entities
        List<User> users = Collections.emptyList();

        // Set up the mocked repository
        doReturn(users).when(userRepository).findAll();

        // Execute the service call
        List<UserDTO> returnedUserList = userService.findAll();

        Assertions.assertTrue(returnedUserList.isEmpty(), "Returned list must be empty");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("User Service: delete user entity found list - Success")
    void deleteWhenUserFoundReturnsSuccess() {
        // Set up mock user entities
        String USER_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        String USER_FULL_NAME = FAKER.name().fullName();
        String[] USER_ROLES = new String[]{"USER"};
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, USER_ROLES);
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, USER_ROLES);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(USER_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.delete(USER_ID);

        Assertions.assertTrue(returnedUser.isPresent(), "Returned entity can not be null");
        Assertions.assertEquals(mockedUserDTO.getId(), returnedUser.get().getId(), "user.id fields are the same");
        Assertions.assertEquals(mockedUserDTO.getFullName(), returnedUser.get().getFullName(), "user.fullName fields are the same");

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("User Service: delete user entity found list - Null entity")
    void deleteWhenUserNotFoundReturnsNullEntity() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.delete(COMPONENT_ID);

        Assertions.assertFalse(returnedUser.isPresent(), "Returned entity must be null");

        verify(userRepository, times(1)).findById(COMPONENT_ID);
    }

    @Test
    @DisplayName("User Service: update user when user does exist - Success")
    void updateWhenUserFoundReturnsSuccess() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(COMPONENT_ID);
        mockedUserDTO.setFullName("Old name");
        User mockedUserEntity = getMockFactory().newUserEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.update(COMPONENT_ID, mockedUserDTO);

        Assertions.assertTrue(returnedUser.isPresent(), "User entity is not null");
        Assertions.assertEquals(mockedUserDTO.getFullName(), returnedUser.get().getFullName(), "FullName must be 'Old name'");

        verify(userRepository, times(1)).findById(COMPONENT_ID);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("User Service: update user when user does not exist - Failure")
    void updateWhenUserEntityNotFoundReturnsFailure() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = getMockFactory().newUserDTO(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.update(COMPONENT_ID, mockedUserDTO);

        Assertions.assertFalse(returnedUser.isPresent(), "User entity must be null");

        verify(userRepository, times(1)).findById(COMPONENT_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("User Service: update user when user does exist and role list is updated - Success")
    void updateWhenUserEntityFoundAndRolesIsUpdatedReturnsSuccess() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = new UserDTO();
        mockedUserDTO.setRoles(Set.of(new String[]{Role.USER.name(), Role.ADMIN.name()}));
        User userEntity = spy(getMockFactory().newUserEntity(COMPONENT_ID));

        // Set up the mocked repository
        doReturn(Optional.of(userEntity)).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.update(COMPONENT_ID, mockedUserDTO);

        Assertions.assertTrue(returnedUser.isPresent(), "User entity must be null");
        Assertions.assertEquals(returnedUser.get().getRoles().size(), 2, String.format("User entity must have %d roles", 2));

        verify(userRepository, times(1)).findById(COMPONENT_ID);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("User Service: update user when user does exist and role list is updated - Success")
    void updateWhenUserEntityFoundAndAdminRoleAddedUpdatedReturnsSuccess() {
        // Set up mock user entities
        String COMPONENT_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        UserDTO mockedUserDTO = new UserDTO();
        mockedUserDTO.setRoles(Set.of(new String[]{Role.ADMIN.name()}));
        User spyUserEntity = spy(getMockFactory().newUserEntity(COMPONENT_ID));

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntity)).when(userRepository).findById(COMPONENT_ID);

        // Execute the service call
        Optional<UserDTO> returnedUser = userService.update(COMPONENT_ID, mockedUserDTO);

        Assertions.assertTrue(returnedUser.isPresent(), "User entity must be null");
        Assertions.assertEquals(returnedUser.get().getRoles().size(), 2, String.format("User entity must have %d roles", 2));
        Assertions.assertTrue(returnedUser.get().getRoles().contains(Role.USER.name()), String.format("Role %s must be in the list of roles", Role.USER.name()));

        verify(userRepository, times(1)).findById(COMPONENT_ID);
        verify(userRepository, times(1)).save(any());
        verify(spyUserEntity, times(1)).setUpdated(any());
    }
}
