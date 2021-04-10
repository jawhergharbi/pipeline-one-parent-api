package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class AccountControllerTodoTest extends BaseLightControllerTest<AccountDTO, Account, AccountService, AccountMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    public AccountControllerTodoTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory,
                ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI,
                DBConstants.ACCOUNT_DOCUMENT,
                service);
    }
}
