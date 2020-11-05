package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterReq;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "unit-tests")
public class UserAuthJwtControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthJwtService service;

    @Test
    @DisplayName("POST /api/auth/register: register user - Success")
    void registerWhenUserAndPasswordMatchesReturnsSuccess() throws Exception {
        // Setup mock authentication entity
        String AUTH_EMAIL = FAKER.internet().emailAddress();
        String AUTH_PASSWORD = FAKER.internet().password();
        String AUTH_FULL_NAME = FAKER.name().fullName();
        AuthJwtRegisterReq postRegister = getMockFactory().newAuthRegisterReq(AUTH_EMAIL, AUTH_PASSWORD, AUTH_PASSWORD, AUTH_FULL_NAME);
        UserAuthDTO mockUserAuth = getMockFactory().newUserAuthDTO(AUTH_EMAIL, AUTH_PASSWORD, Role.ADMIN.name());


        // setup the mocked service
        doReturn(mockUserAuth).when(service).create(postRegister);

        // execute the POST request
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is(AUTH_EMAIL)));

        ArgumentCaptor<AuthJwtRegisterReq> authRequestCaptor = ArgumentCaptor.forClass(AuthJwtRegisterReq.class);
        verify(service, times(1)).create(authRequestCaptor.capture());
        Assertions.assertEquals(AUTH_EMAIL, authRequestCaptor.getValue().getEmail());
        Assertions.assertEquals(AUTH_FULL_NAME, authRequestCaptor.getValue().getFullName());
        Assertions.assertEquals(AUTH_PASSWORD, authRequestCaptor.getValue().getPassword());
    }
}
