package com.sawoo.pipeline.api.service.user;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserAuthJwtUserDetailsServiceTest {

    private final Faker FAKER;
    private final UserMockFactory mockFactory;

    @Autowired
    private UserAuthJwtUserDetailsServiceImpl service;

    @MockBean
    private UserRepository repository;

    @Autowired
    public UserAuthJwtUserDetailsServiceTest(UserMockFactory mockFactory) {
        this.mockFactory = mockFactory;
        FAKER = Faker.instance();
    }

    @Test
    @DisplayName("loadUserByUsername: user found - Success")
    void loadUserByUsernameWhenUserFoundReturnsSuccess() {
        // Set up mock entity
        String USER_EMAIL = FAKER.internet().emailAddress();
        String USER_PASSWORD = FAKER.internet().password(6, 12);
        User mockedUserAuth = mockFactory.newEntity(USER_EMAIL, USER_PASSWORD);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUserAuth)).when(repository).findByEmail(USER_EMAIL);

        // Execute the service call
        UserDetails userDetails = service.loadUserByUsername(USER_EMAIL);

        Assertions.assertNotNull(userDetails, "UserDetails can not be null");
        Assertions.assertEquals(
                USER_EMAIL,
                userDetails.getUsername(),
                String.format("Email must be the same: [%s]", USER_EMAIL));
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
