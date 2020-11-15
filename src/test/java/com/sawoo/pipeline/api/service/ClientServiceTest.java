package com.sawoo.pipeline.api.service;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ClientException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.UserOld;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import com.sawoo.pipeline.api.repository.client.datastore.ClientRepository;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientServiceTest extends BaseServiceTestOld {

    @Autowired
    private DataStoreKeyFactory dataStoreKeyFactory;

    @Autowired
    private ClientService service;

    @MockBean
    private ClientRepository repository;

    @SpyBean
    private UserAuthService userService;

    @Test
    @DisplayName("Client Service: update CSM when client does exist - Success")
    void updateCSMWhenClientFoundAndUserFoundReturnsSuccess() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client mockedClientEntity = getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true);
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTOOld mockedUserDTOOld = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.MNG.name()});

        // Set up the mocked repository
        doReturn(Optional.of(mockedClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTOOld).when(userService).findById(USER_ID);

        // Execute the service call
        Optional<ClientBasicDTO> returnedDTO = service.updateCSM(CLIENT_ID, USER_ID);

        Assertions.assertTrue(returnedDTO.isPresent(), "Client can not be null");
        Assertions.assertNotNull(returnedDTO.get().getCustomerSuccessManager(), "Customer Success Manager can not be null");
        Assertions.assertEquals(
                USER_ID,
                returnedDTO.get().getCustomerSuccessManager().getId(),
                String.format("Customer Success Manager id must be %s", USER_ID));

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(userService, times(1)).findById(USER_ID);
    }

    @Test
    @DisplayName("Client Service: update CSM when client does exist and SA matches CSM - Failure")
    void updateCSMWhenClientFoundCSMDoesMatchSAReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserOld mockedUserOldEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.AST.name(), Role.MNG.name()});
        spyClientEntity.setSalesAssistant(mockedUserOldEntity);

        UserDTOOld mockedUserDTOOld = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.AST.name(), Role.MNG.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTOOld).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateCSM(CLIENT_ID, USER_ID),
                "updateCSM must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_CSM_MATCH_SA_EXCEPTION);
        Assertions.assertEquals(3, exception.getArgs().length);

        verify(spyClientEntity, never()).setCustomerSuccessManager(any());
    }

    @Test
    @DisplayName("Client Service: update SA when client does exist and CSM matches SA - Failure")
    void updateSAWhenClientFoundCSMDoesMatchSAReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserOld mockedUserOldEntity = getMockFactory().newUserEntity(USER_ID, USER_FULL_NAME, new String[]{Role.MNG.name(), Role.AST.name()});
        spyClientEntity.setCustomerSuccessManager(mockedUserOldEntity);

        UserDTOOld mockedUserDTOOld = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.MNG.name(), Role.AST.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTOOld).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateSA(CLIENT_ID, USER_ID),
                "updateSA must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_SA_MATCH_CSM_EXCEPTION);
        Assertions.assertEquals(3, exception.getArgs().length);

        verify(spyClientEntity, never()).setSalesAssistant(any());
    }

    @Test
    @DisplayName("Client Service: update CSM when client does exist - Failure")
    void updateCSMWhenClientFoundAndUserRolesIsNotCorrectReturnsFailure() {
        // Set up mocked entities
        Long CLIENT_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String CLIENT_FULL_NAME = FAKER.name().fullName();
        String CLIENT_LINKED_IN_URL = FAKER.internet().url();
        Client spyClientEntity = spy(getMockFactory().newClientEntity(CLIENT_ID, CLIENT_FULL_NAME, CLIENT_LINKED_IN_URL, true));
        String USER_ID = FAKER.lorem().fixedString(16);
        String USER_FULL_NAME = FAKER.name().fullName();
        UserDTOOld mockedUserDTOOld = getMockFactory().newUserDTO(USER_ID, USER_FULL_NAME, new String[]{Role.MNG.name()});

        // Set up the mocked repository
        doReturn(Optional.of(spyClientEntity)).when(repository).findById(CLIENT_ID);
        doReturn(mockedUserDTOOld).when(userService).findById(USER_ID);

        ClientException exception = Assertions.assertThrows(
                ClientException.class,
                () -> service.updateSA(CLIENT_ID, USER_ID),
                "updateSA must throw a ClientException");

        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.CLIENT_UPDATE_SA_MUST_HAVE_ROLE_SA_EXCEPTION);
        Assertions.assertEquals(4, exception.getArgs().length);

        verify(spyClientEntity, never()).setCustomerSuccessManager(any());

        verify(repository, times(1)).findById(CLIENT_ID);
        verify(userService, times(1)).findById(USER_ID);
    }

    private Key createKey(Long leadId, Long interactionId) {
        Key parentKey = dataStoreKeyFactory.getKeyFactory(DataStoreConstants.LEAD_DOCUMENT).newKey(leadId);
        return Key.newBuilder(parentKey, DataStoreConstants.LEAD_ACTION_DOCUMENT, interactionId).build();
    }
}
