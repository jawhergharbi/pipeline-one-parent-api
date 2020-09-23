package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.repository.client.datastore.ClientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ClientRepositoryTest {

    private static final File CLIENT_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "client-test-data.json").toFile();
    private static final List<Long> clientIdList = new ArrayList<>();
    private final Faker FAKER = Faker.instance();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Client[] clientList = mapper.readValue(CLIENT_JSON_DATA, Client[].class);

        // Load each auth entity into the dataStore
        Arrays.stream(clientList).forEach((client) -> {
            clientIdList.add(client.getId());
            datastoreTemplate.save(client);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAllById(clientIdList, Client.class);
        clientIdList.clear();
    }

    @Test
    void findAllWhenTwoEntitiesFoundReturnsSuccess() {
        Iterable<Client> auths = repository.findAll();

        Assertions.assertEquals(
                clientIdList.size(),
                (int) StreamSupport
                        .stream(auths.spliterator(), true).count(),
                String.format("Should be %d Client entities in the database", clientIdList.size()));
    }

    @Test
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        Long CLIENT_ID = 987L;
        Optional<Client> entity = repository.findById(CLIENT_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("Client with [id]: %s can not be null", CLIENT_ID));
        Assertions.assertEquals(CLIENT_ID, entity.get().getId(), String.format("Client [id] must be %s", CLIENT_ID));
    }

    @Test
    void findByNameWhenEntityIdFoundReturnsSuccess() {
        String CLIENT_LINKED_IN_URL = "http://linkedin.com/miguelmaquieira";
        Optional<Client> entity = repository.findByLinkedInUrl(CLIENT_LINKED_IN_URL);

        Assertions.assertTrue(entity.isPresent(), String.format("Client with [linkedInUrl]: %s can not be null", CLIENT_LINKED_IN_URL));
        Assertions.assertEquals(CLIENT_LINKED_IN_URL, entity.get().getLinkedInUrl(), String.format("Client [name] must be %s", CLIENT_LINKED_IN_URL));
    }

    @Test
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        Long CLIENT_ID = 12345L;
        Optional<Client> entity = repository.findById(CLIENT_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("Client with [id]: %s can be found", CLIENT_ID));
    }

    @Test
    void saveWhenAddNewEntityReturnsSuccess() {
        Client client = new Client();
        client.setFullName(FAKER.name().fullName());
        client.setLinkedInUrl(FAKER.internet().url());
        client.setEmail(FAKER.internet().emailAddress());

        repository.save(client);
        clientIdList.add(client.getId());

        Iterable<Client> clients = repository.findAll();
        Assertions.assertEquals(
                clientIdList.size(),
                (int) StreamSupport
                        .stream(clients.spliterator(), true).count(),
                String.format("Should be [%d] client entities in the database", clientIdList.size()));
        Assertions.assertNotNull(client.getId(), "Client [id] can not be null after being saved");
    }
}
