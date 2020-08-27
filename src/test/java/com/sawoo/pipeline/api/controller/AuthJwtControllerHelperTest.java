package com.sawoo.pipeline.api.controller;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.service.AuthJwtUserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AuthJwtControllerHelperTest {

    @Autowired
    private AuthJwtControllerHelper controllerHelper;

    @MockBean
    private AuthJwtUserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("token: user exists- Success")
    void tokenWhenUserExistsReturnsSuccess() {
        // Set up mock entity
        String username = Faker.instance().internet().emailAddress();
        String password = "my_password";

        // Execute the service call
        //String returnedToken = controllerHelper.token(username, password);
    }
}
