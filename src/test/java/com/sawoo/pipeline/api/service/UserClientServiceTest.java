package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.common.exceptions.UserClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.ClientRepository;
import com.sawoo.pipeline.api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class UserClientServiceTest extends BaseServiceTest {

    @Autowired
    private UserClientService service;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("UserClient Service: findAll - Success")
    void findAllWhenThereAreThreeClientsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        List<Client> clientList = IntStream.range(0, listSize)
                .mapToObj((client) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    return getMockFactory().newClientEntity(CLIENT_ID);
                }).collect(Collectors.toList());
        String USER_ID = FAKER.name().username();
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID);
        mockedUserEntity.setClients(clientList);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(anyString());

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAll(USER_ID);

        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedList.size(), String.format("Returned list size must be %d", listSize));

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, atMostOnce()).findById(userIdCaptor.capture());

        Assertions.assertEquals(userIdCaptor.getValue(), USER_ID, String.format("User id to be verified must be: [%s]", USER_ID));
    }

    @Test
    @DisplayName("UserClient Service: findAll - Success")
    void findAllWhenUserRoleADMINThereAreThreeClientsReturnsSuccess() {
        // Set up mock entities
        int listSize = 3;
        String USER_ID_CSM = FAKER.regexify(FAKER_USER_ID_REGEX);
        String USER_FULL_NAME_CSM = FAKER.name().fullName();
        User userCSM = getMockFactory().newUserEntity(USER_ID_CSM, USER_FULL_NAME_CSM, new String[]{Role.CSM.name(), Role.USER.name()});
        String USER_ID_ADMIN = FAKER.regexify(FAKER_USER_ID_REGEX);
        String USER_FULL_NAME_ADMIN = FAKER.name().fullName();
        User userADMIN = getMockFactory().newUserEntity(USER_ID_ADMIN, USER_FULL_NAME_ADMIN, new String[]{Role.ADMIN.name(), Role.USER.name()});
        List<Client> clientList = IntStream.range(0, listSize)
                .mapToObj((client) -> {
                    Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
                    return getMockFactory().newClientEntity(CLIENT_ID);
                }).collect(Collectors.toList());
        clientList.get(0).setCustomerSuccessManager(userCSM);
        userCSM.setClients(Collections.singletonList(clientList.get(0)));

        // Set up the mocked repository
        doReturn(Optional.of(userCSM)).doReturn(Optional.of(userADMIN)).when(userRepository).findById(anyString());
        doReturn(clientList).when(clientRepository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedListCSM = service.findAll(USER_ID_CSM);
        List<ClientBasicDTO> returnedListADMIN = service.findAll(USER_ID_ADMIN);

        Assertions.assertFalse(returnedListCSM.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedListCSM.size(), String.format("Returned list size must be %d", listSize));

        Assertions.assertFalse(returnedListADMIN.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedListCSM.size(), String.format("Returned list size must be %d", listSize));
    }

    @Test
    @DisplayName("UserClient Service: findAll empty list - Success")
    void findAllWhenThereAreNoClientReturnsSuccess() {
        // Set up mock entities
        String USER_ID = FAKER.name().username();
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(anyString());

        // Execute the service call
        List<ClientBasicDTO> returnedList = service.findAll(USER_ID);

        // Assertions
        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");
    }

    @Test
    @DisplayName("UserClient Service: findAll user not found - Failure")
    void findAllWhenUserNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        String USER_ID = FAKER.name().username();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findAll(USER_ID),
                "findAll must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, atMostOnce()).findById(userIdCaptor.capture());

        Assertions.assertEquals(userIdCaptor.getValue(), USER_ID, String.format("User id to be verified must be: [%s]", USER_ID));
    }

    @Test
    @DisplayName("UserClient Service: add client when user and client found - Success")
    void addWhenUserFoundAndClientFoundReturnsSuccess() {
        // Set up mocked entities
        String USER_ID = FAKER.name().username();
        String USER_FULL_NAME = FAKER.name().fullName();
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        User spyUserEntity = spy(getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name()}));

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntity)).when(userRepository).findById(anyString());
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(spyUserEntity).when(userRepository).save(any());
        doReturn(spyClientEntity).when(clientRepository).save(any());

        // Execute the service call
        ClientBasicDTO returnedEntity = service.add(USER_ID, CLIENT_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Client entity can not be null");

        verify(spyUserEntity, times(1)).getClients();
        verify(spyUserEntity, times(1)).setUpdated(any());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(spyClientEntity, times(1)).setSalesAssistant(spyUserEntity);
        verify(spyClientEntity).setSalesAssistant(userCaptor.capture());
        Assertions.assertEquals(userCaptor.getValue().getId(), USER_ID, String.format("User id to be verified must be: [%s]", USER_ID));
        verify(spyClientEntity, times(1)).setUpdated(any());
    }

    @Test
    @DisplayName("UserClient Service: add client when user and client found but user is already the CSM - Failure")
    void addWhenUserFoundAndClientFoundButUserIsCSMReturnsFailure() {
        // Set up mocked entities
        String USER_ID = FAKER.name().username();
        String USER_FULL_NAME = FAKER.name().fullName();
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        User spyUserEntity = spy(getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name(), Role.CSM.name()}));
        spyClientEntity.setCustomerSuccessManager(spyUserEntity);

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntity)).when(userRepository).findById(anyString());
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());

        // Execute the service call
        UserClientException exception = Assertions.assertThrows(
                UserClientException.class,
                () -> service.add(USER_ID, CLIENT_ID),
                "findAll must throw a UserClientException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_USER_ALREADY_ADDED_EXCEPTION);
        Assertions.assertEquals(6, exception.getArgs().length);

        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("UserClient Service: add client when user and client found but user is already added - Failure")
    void addWhenUserFoundAndClientFoundButUserIsAlreadyAddedReturnsFailure() {
        // Set up mocked entities
        String USER_ID = FAKER.name().username();
        String USER_FULL_NAME = FAKER.name().fullName();
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        User spyUserEntity = spy(getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name()}));
        spyClientEntity.setSalesAssistant(spyUserEntity);

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntity)).when(userRepository).findById(anyString());
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());

        // Execute the service call
        UserClientException exception = Assertions.assertThrows(
                UserClientException.class,
                () -> service.add(USER_ID, CLIENT_ID),
                "findAll must throw a UserClientException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.USER_CLIENT_ADD_CLIENT_USER_ALREADY_ADDED_EXCEPTION);
        Assertions.assertEquals(6, exception.getArgs().length);

        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("UserClient Service: add client when user and client found and roles is already assigned to another user - Success")
    void addWhenUserFoundAndClientFoundAndRolesIsAlreadyAssignedToAnotherUserReturnsSuccess() {
        // Set up mocked entities
        String USER_ID_NEW_SA = FAKER.name().username();
        String USER_FULL_NAME_NEW_SA = FAKER.name().fullName();
        String USER_ID_OLD_SA = FAKER.name().username();
        String USER_FULL_NAME_OLD_SA = FAKER.name().fullName();
        User spyUserEntityNew = spy(getMockFactory().newUserEntity(USER_ID_NEW_SA, USER_FULL_NAME_NEW_SA, new String[]{Role.SA.name()}));
        User mockedUserEntityOld = getMockFactory().newUserEntity(USER_ID_OLD_SA, USER_FULL_NAME_OLD_SA, new String[]{Role.SA.name()});
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID));
        spyClientEntity.setSalesAssistant(mockedUserEntityOld);

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntityNew))
                .doReturn(Optional.of(mockedUserEntityOld))
                .when(userRepository).findById(anyString());
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(spyClientEntity).when(clientRepository).save(any());
        doReturn(mockedUserEntityOld)
                .doReturn(spyUserEntityNew)
                .when(userRepository).save(any());

        // Execute the service call
        ClientBasicDTO returnedEntity = service.add(USER_ID_NEW_SA, CLIENT_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Client entity can not be null");

        verify(userRepository, times(2)).findById(anyString());

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, times(2)).findById(userIdCaptor.capture());
        Assertions.assertEquals(2, userIdCaptor.getAllValues().size(),
                String.format("User repository must call findById with [%d] different user ids", 2));
        Assertions.assertTrue(
                userIdCaptor.getAllValues().contains(USER_ID_NEW_SA),
                String.format("User repository must call findById with user id [%s] at least once", USER_FULL_NAME_NEW_SA));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userCaptor.capture());
        Assertions.assertEquals(2, userCaptor.getAllValues().size(),
                String.format("User repository must call save with [%d] different user", 2));
        Assertions.assertTrue(
                userCaptor.getAllValues().contains(mockedUserEntityOld),
                String.format("User repository must call save with user [%s] at least once", mockedUserEntityOld));
    }

    @Test
    @DisplayName("UserClient Service: remove client when user and client found - Success")
    void removeWhenUserFoundAndClientFoundReturnsSuccess() {
        // Set up mocked entities
        String USER_ID = FAKER.name().username();
        String USER_FULL_NAME = FAKER.name().fullName();
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        Client mockedClientEntity = getMockFactory().newClientEntity(CLIENT_ID);
        Client spyClientEntity = spy(mockedClientEntity);
        User spyUserEntity = spy(getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.SA.name()}));
        mockedClientEntity.setSalesAssistant(spyUserEntity);

        // Set up the mocked repository
        doReturn(Optional.of(spyUserEntity)).when(userRepository).findById(anyString());
        doReturn(Optional.of(spyClientEntity)).when(clientRepository).findById(anyLong());
        doReturn(spyUserEntity).when(userRepository).save(any());
        doReturn(spyClientEntity).when(clientRepository).save(any());

        // Execute the service call
        ClientBasicDTO returnedEntity = service.remove(USER_ID, CLIENT_ID);

        // Assertions
        Assertions.assertNotNull(returnedEntity, "Client entity can not be null");

        verify(spyUserEntity, atMostOnce()).getClients();
        verify(spyUserEntity, atMostOnce()).setUpdated(any());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(spyClientEntity, atMostOnce()).setSalesAssistant(userCaptor.capture());
        Assertions.assertNull(userCaptor.getValue(), "setSalesAssistant must be call with argument null");
    }
}
