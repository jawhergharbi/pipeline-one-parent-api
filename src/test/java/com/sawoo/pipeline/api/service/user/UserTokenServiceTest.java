package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.mock.UserTokenMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.repository.user.UserTokenRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTokenServiceTest extends BaseServiceTest<UserTokenDTO, UserToken, UserTokenRepository, UserTokenService, UserTokenMockFactory> {

    @MockBean
    private UserTokenRepository repository;

    @Autowired
    public UserTokenServiceTest(UserTokenMockFactory mockFactory, UserTokenService service) {
        super(mockFactory, DBConstants.USER_TOKEN_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(UserToken component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(UserTokenDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(UserToken entity) {
        doReturn(Optional.of(entity)).when(repository).findByToken(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("findByToken: entity found - Success")
    void findByTokenWhenEntityExitsReturnsSuccess() {
        // Set up mock entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String TOKEN = getMockFactory().getFAKER().internet().uuid();
        UserToken mockedEntity = getMockFactory().newEntity(COMPONENT_ID);
        mockedEntity.setToken(TOKEN);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findByToken(anyString());

        // Execute the service call
        Optional<UserTokenDTO> returnedEntity = getService().findByToken(TOKEN);

        // Assert the response
        Assertions.assertTrue(
                returnedEntity.isPresent(),
                String.format("UserToken entity with token [%s] was not found", TOKEN));
        returnedEntity.ifPresent((ut) -> Assertions.assertEquals(
                COMPONENT_ID,
                ut.getId(),
                String.format("UserToken with token [%s] must have id [%s]", TOKEN, COMPONENT_ID)
        ));

        verify(repository, times(1)).findByToken(TOKEN);
    }

    @Test
    @DisplayName("findByToken: entity not found return optional empty - Failure")
    void findByTokenWhenTokenDoesNotExitsReturnsSuccess() {
        // Set up mock entities
        String TOKEN = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByToken(anyString());

        // Execute the service call
        Optional<UserTokenDTO> returnedEntity = getService().findByToken(TOKEN);

        // Assert the response
        Assertions.assertFalse(
                returnedEntity.isPresent(),
                String.format("UserToken entity with token [%s] was found", TOKEN));

        verify(repository, times(1)).findByToken(TOKEN);
    }
}
