package com.sawoo.pipeline.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.auth.register.AuthJwtRegisterRequestNebular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
@SpringBootTest
class AuthJwtControllerNebularTest {

    private final Faker FAKER = Faker.instance();

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private AuthJwtControllerHelper controllerHelper;

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /api/auth/register: Invalid request body. Email is null - Failure")
    void egisterWhenRequestBodyInvalidEmailNullReturnsFailure() throws Exception {
        // Setup mock Dummy Entity
        AuthJwtRegisterRequestNebular postRegister =
                new AuthJwtRegisterRequestNebular(
                        null,
                        "my_password",
                        "my_password",
                        FAKER.name().fullName(),
                        null,
                        0);


        // execute the POST request
        mockMvc.perform(post("/api/auth/nebular/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRegister)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath(
                                "$.messages[0]",
                                containsString("Field or param [email] in component [authJwtRegisterRequestNebular] can not be empty")));
    }

}
