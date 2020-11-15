package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.service.company.CompanyService;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        // update method does not be called
        Assertions.assertTrue(true, "Override to avoid super class call");
    }
}
