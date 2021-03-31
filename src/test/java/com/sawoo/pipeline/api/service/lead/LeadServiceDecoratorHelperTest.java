package com.sawoo.pipeline.api.service.lead;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class LeadServiceDecoratorHelperTest {

    private final Faker FAKER;
    private final UserMockFactory mockFactory;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    public LeadServiceDecoratorHelperTest(UserMockFactory mockFactory) {
        this.mockFactory = mockFactory;
        FAKER = Faker.instance();
    }
}
