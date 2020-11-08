package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class ProspectRepositoryTest extends BaseRepositoryTest {

    private static final File LEAD_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "prospect-test-data.json").toFile();
    private int documentSize;

    @Autowired
    private ProspectRepository repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Prospect[] leadList = mapper.readValue(LEAD_JSON_DATA, Prospect[].class);
        documentSize = leadList.length;


        // Load each entity into the DB
        repository.insert(Arrays.asList(leadList));
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        repository.deleteAll();
    }

    @Test
    @DisplayName("findAll: return all the entities defined in the test file - Success")
    void findAllReturnsSuccess() {
        List<Prospect> prospects = repository.findAll();

        Assertions.assertEquals(
                documentSize,
                prospects.size(),
                String.format("Should be %d Prospect entities in the database", documentSize));
        Assertions.assertNotNull(
                prospects.get(0).getCompany(),
                "Company of the first element in the list can not be null");
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String PROSPECT_ID = "5fa3ce63rt4ef23d963da45b";
        Optional<Prospect> entity = repository.findById(PROSPECT_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("Prospect with [id]: %s can not be null", PROSPECT_ID));
        Assertions.assertEquals(PROSPECT_ID, entity.get().getId(), String.format("Prospect [id] must be %s", PROSPECT_ID));
    }
}
