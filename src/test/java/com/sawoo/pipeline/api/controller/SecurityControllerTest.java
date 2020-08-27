package com.sawoo.pipeline.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /security/public/hello: hello end point is open - Success")
    void getHelloWhenNoRoleAndNoAuthenticationProvidedReturnsSuccess() throws Exception {
        // Execute the GET request
        mockMvc
                .perform(get("/security/public/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("Hello")))
                .andExpect(content().string(containsString("Public")));
    }

    @Test
    @DisplayName("GET /security/private/hello: unauthorized access - Failure")
    void getHelloWhenNoAuthenticationProvidedReturnsFailure() throws Exception {
        // Execute the GET request
        mockMvc
                .perform(get("/security/private/main/hello"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser("miguel")
    @Test
    @DisplayName("GET /security/authorization: authorized access - Success")
    void getHelloWhenAuthenticationProvidedReturnsSuccess() throws Exception {
        // Execute the GET request
        mockMvc
                .perform(get("/security/private/main/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("Hello")));
    }

    @WithMockUser(username = "miguelito")
    @Test
    @DisplayName("GET /security/authorization: authorized access - Success")
    void getHelloWhenAuthenticationProvidedForDifferentUserReturnsSuccess() throws Exception {
        // Execute the GET request
        mockMvc
                .perform(get("/security/private/main/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("Hello")));
    }

    @WithMockUser(username = "miguelito", roles = "USER")
    @Test
    @DisplayName("GET /security/private/admin: unauthorized access - Failure")
    void getAdminWhenAuthenticationProvidedAndAuthorizationNotProvidedReturnsFailure() throws Exception {
        // Execute the GET request
        mockMvc
                .perform(get("/security/private/admin/hello"))
                .andExpect(status().isUnauthorized());
    }

}
