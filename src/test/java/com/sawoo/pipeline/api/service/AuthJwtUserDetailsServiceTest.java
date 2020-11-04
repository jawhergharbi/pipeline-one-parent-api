package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.model.Authentication;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.repository.AuthRepository;
import com.sawoo.pipeline.api.repository.UserRepository;
import com.sawoo.pipeline.api.service.auth.AuthJwtUserDetailsServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AuthJwtUserDetailsServiceTest extends BaseServiceTest {

    @Autowired
    private AuthJwtUserDetailsServiceImpl service;

    @MockBean
    private AuthRepository authRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("loadUserByUsername: user found - Success")
    void loadUserByUsernameWhenUserFoundReturnsSuccess() {
        // Set up mock entity
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);
        String AUTH_IDENTIFIER = FAKER.name().username();
        Authentication mockedAuth = getMockFactory().newAuthenticationEntity(AUTH_ID, AUTH_IDENTIFIER);
        User mockedUser = getMockFactory().newUserEntity(AUTH_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedAuth)).when(authRepository).findByIdentifier(AUTH_IDENTIFIER);
        doReturn(Optional.of(mockedUser)).when(userRepository).findById(AUTH_ID);

        // Execute the service call
        UserDetails userDetails = service.loadUserByUsername(AUTH_IDENTIFIER);

        Assertions.assertNotNull(userDetails, "UserDetails can not be null");
        Assertions.assertEquals(
                AUTH_IDENTIFIER,
                userDetails.getUsername(),
                String.format("Username must be the same: [%s]", AUTH_IDENTIFIER));
        Assertions.assertEquals(
                mockedAuth.getPassword(),
                userDetails.getPassword(),
                String.format("Password must be the same: [%s]", mockedAuth.getPassword()));
        Assertions.assertEquals(
                1,
                userDetails.getAuthorities().size(),
                String.format("Authorities must be greater than [%d]", 1));

        verify(authRepository, times(1)).findByIdentifier(anyString());
        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("loadUserByUsername: authorization not found - Success")
    void loadUserByUsernameWhenAuthorizationNotFoundReturnsSuccess() {
        // Set up mock entity
        String AUTH_IDENTIFIER = FAKER.name().username();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(authRepository).findByIdentifier(AUTH_IDENTIFIER);

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(AUTH_IDENTIFIER),
                "loadUserByUsername must throw an UsernameNotFoundException");
    }

    @Test
    @DisplayName("loadUserByUsername: user not found - Success")
    void loadUserByUsernameWhenUserNotFoundReturnsSuccess() {
        // Set up mock entity
        String AUTH_ID = FAKER.regexify(FAKER_USER_ID_REGEX);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(AUTH_ID);

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(AUTH_ID),
                "loadUserByUsername must throw an UsernameNotFoundException");
    }
}
