package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class UserControllerTest extends BaseControllerTest<UserAuthDTO, User, UserAuthService, UserMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService service;

    @Autowired
    public UserControllerTest(UserMockFactory mockFactory, UserAuthService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.USER_CONTROLLER_API_BASE_URI,
                DataStoreConstants.USER_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "email";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("email", "fullName", "roles", "created");
    }

    @Override
    protected void updateWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // update method can not be called
        Assertions.assertTrue(true, "Override to avoid super class call");
    }

    @Test
    @DisplayName("POST /api/auth: create user - Success")
    void createWhenUserAndPasswordMatchesReturnsSuccess() throws Exception {
        // Setup mock authentication entity
        String USER_AUTH_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        String USER_AUTH_PASSWORD = getMockFactory().getFAKER().internet().password(6, 12);
        String USER_AUTH_FULL_NAME = getMockFactory().getFAKER().name().fullName();
        UserAuthDTO postEntity = getMockFactory().newDTO(
                null,
                USER_AUTH_EMAIL,
                USER_AUTH_PASSWORD,
                USER_AUTH_PASSWORD,
                USER_AUTH_FULL_NAME,
                new String[]{Role.ADMIN.name()});
        String USER_AUTH_ID = getMockFactory().getComponentId();
        UserAuthDTO mockedEntity = getMockFactory().newDTO(USER_AUTH_ID, postEntity);
        mockedEntity.setPassword(null);
        mockedEntity.setConfirmPassword(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(postEntity);

        // execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is(USER_AUTH_EMAIL)));

        ArgumentCaptor<UserAuthDTO> authRequestCaptor = ArgumentCaptor.forClass(UserAuthDTO.class);
        verify(service, times(1)).create(authRequestCaptor.capture());
        Assertions.assertEquals(USER_AUTH_EMAIL, authRequestCaptor.getValue().getEmail());
        Assertions.assertEquals(USER_AUTH_FULL_NAME, authRequestCaptor.getValue().getFullName());
        Assertions.assertEquals(USER_AUTH_PASSWORD, authRequestCaptor.getValue().getPassword());
    }
}
