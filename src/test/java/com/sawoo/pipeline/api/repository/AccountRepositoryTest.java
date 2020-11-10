package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AccountRepositoryTest extends BaseRepositoryTest<Account, AccountRepository, AccountMockFactory> {

    private static final File ACCOUNT_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "account-test-data.json").toFile();
    private static final String ACCOUNT_ID = "5fa3ce63rt4ef23d963da45b";

   @Autowired
   public AccountRepositoryTest(AccountRepository repository, AccountMockFactory mockFactory) {
        super(repository, ACCOUNT_JSON_DATA, ACCOUNT_ID, Account.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Account[]> getClazz() {
        return Account[].class;
    }

    @Override
    protected String getComponentId(Account component) {
        return component.getId();
    }

    @Override
    protected Account getNewEntity() {
        String ACCOUNT_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(ACCOUNT_ID);
    }

    /*@Test
    void findByNameWhenEntityIdFoundReturnsSuccess() {
        String CLIENT_LINKED_IN_URL = "http://linkedin.com/miguelmaquieira";
        Optional<Client> entity = getRepository().findByLinkedInUrl(CLIENT_LINKED_IN_URL);

        Assertions.assertTrue(entity.isPresent(), String.format("Client with [linkedInUrl]: %s can not be null", CLIENT_LINKED_IN_URL));
        Assertions.assertEquals(CLIENT_LINKED_IN_URL, entity.get().getLinkedInUrl(), String.format("Client [name] must be %s", CLIENT_LINKED_IN_URL));
    }*/
}
