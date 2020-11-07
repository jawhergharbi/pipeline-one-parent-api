package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.UserRepositoryMongo;
import com.sawoo.pipeline.api.service.user.UserAuthJwtUserDetailsServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthJwtUserDetailsServiceTest extends BaseServiceTest {

    @Autowired
    private UserAuthJwtUserDetailsServiceImpl service;

    @MockBean
    private UserRepositoryMongo repository;

    @Test
    @DisplayName("loadUserByUsername: user found - Success")
    void loadUserByUsernameWhenUserFoundReturnsSuccess() {
        // Set up mock entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        UserMongoDB mockedUserAuth = getMockFactory().newUserAuthEntity(AUTH_EMAIL);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findByEmail(AUTH_EMAIL);

        // Execute the service call
        UserDetails userDetails = service.loadUserByUsername(AUTH_EMAIL);

        Assertions.assertNotNull(userDetails, "UserDetails can not be null");
        Assertions.assertEquals(
                AUTH_EMAIL,
                userDetails.getUsername(),
                String.format("Email must be the same: [%s]", AUTH_EMAIL));
        Assertions.assertEquals(
                mockedUserAuth.getPassword(),
                userDetails.getPassword(),
                String.format("Password must be the same: [%s]", mockedUserAuth.getPassword()));
        Assertions.assertEquals(
                1,
                userDetails.getAuthorities().size(),
                String.format("Authorities must be greater than [%d]", 1));

        verify(repository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("loadUserByUsername: user not found - Failure")
    void loadUserByUsernameWhenUserNotFoundReturnsFailure() {
        // Set up mock entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByEmail(AUTH_EMAIL);

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(AUTH_EMAIL),
                "loadUserByUsername must throw an UsernameNotFoundException");
    }
}
