package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.UserRepository;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
    private ClientRepositoryWrapper clientRepository;

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
        String USER_FULL_NAME = FAKER.name().fullName();
        User mockedUserEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[] {Role.CSM.name()});

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserEntity)).when(userRepository).findById(anyString());
        doReturn(clientList).when(clientRepository).findByUserId(USER_ID);

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

        // Set up the mocked repository
        doReturn(Optional.of(userCSM))
                .doReturn(Optional.of(userADMIN))
                .when(userRepository).findById(anyString());
        doReturn(Collections.singletonList(clientList.get(0)))
                .when(clientRepository).findByUserId(anyString());
        doReturn(clientList)
                .when(clientRepository).findAll();

        // Execute the service call
        List<ClientBasicDTO> returnedListCSM = service.findAll(USER_ID_CSM);
        List<ClientBasicDTO> returnedListADMIN = service.findAll(USER_ID_ADMIN);

        Assertions.assertFalse(returnedListCSM.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(1, returnedListCSM.size(), String.format("Returned list size must be %d", 1));

        Assertions.assertFalse(returnedListADMIN.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedListADMIN.size(), String.format("Returned list size must be %d", listSize));

        verify(userRepository, Mockito.times(2)).findById(any());
        verify(clientRepository, Mockito.atMostOnce()).findAll();
        verify(clientRepository, Mockito.atMostOnce()).findByUserId(anyString());
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
}
