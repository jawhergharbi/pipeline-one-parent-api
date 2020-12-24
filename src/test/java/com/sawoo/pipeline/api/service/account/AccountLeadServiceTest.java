package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountLeadServiceTest extends BaseLightServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @MockBean
    private LeadRepository leadRepository;

    @Autowired
    public AccountLeadServiceTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory, DBConstants.ACCOUNT_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }
}
