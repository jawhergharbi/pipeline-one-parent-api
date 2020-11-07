package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
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
public class CompanyRepositoryTest extends BaseRepositoryTest {

    private static final File COMPANY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "company-test-data.json").toFile();
    private int documentSize;

    @Autowired
    private CompanyRepositoryMongo repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        CompanyMongoDB[] companyList = mapper.readValue(COMPANY_JSON_DATA, CompanyMongoDB[].class);
        documentSize = companyList.length;

        // Load each auth entity into the DB
        repository.insert(Arrays.asList(companyList));
    }

    @AfterEach
    void afterEach() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("findAll: return the entities defined in the file - Success")
    void findAllReturnsSuccess() {
        List<CompanyMongoDB> companies = repository.findAll();

        Assertions.assertEquals(
                documentSize,
                companies.size(),
                String.format("Should be %d Company entities in the database", documentSize));
    }

    @Test
    @DisplayName("findById: entity found - Success")
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        String COMPANY_ID = "5fa3ce63ee4ef64d966da45b";
        Optional<CompanyMongoDB> entity = repository.findById(COMPANY_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("Company with [id]: %s can not be null", COMPANY_ID));
        Assertions.assertEquals(COMPANY_ID, entity.get().getId(), String.format("Company [id] must be %s", COMPANY_ID));
    }

    @Test
    @DisplayName("findByName: entity found - Success")
    void findByNameWhenEntityIdFoundReturnsSuccess() {
        String COMPANY_NAME = "google";
        Optional<CompanyMongoDB> entity = repository.findByName(COMPANY_NAME);

        Assertions.assertTrue(entity.isPresent(), String.format("Company with [name]: %s can not be null", COMPANY_NAME));
        Assertions.assertEquals(COMPANY_NAME, entity.get().getName(), String.format("Company [name] must be %s", COMPANY_NAME));
    }

    @Test
    @DisplayName("findByName: entity not found - Failure")
    void findByIdWhenEntityNotFoundReturnsFailure() {
        String COMPANY_ID = "wrong_id";
        Optional<CompanyMongoDB> entity = repository.findById(COMPANY_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("Company with [id]: %s can not be found", COMPANY_ID));
    }

    @Test
    @DisplayName("save: entity saved - Success")
    void saveWhenAddNewEntityReturnsSuccess() {
        CompanyMongoDB company = getMockFactory()
                .newCompanyEntity(FAKER.company().name(), FAKER.company().url());

        CompanyMongoDB companyStored = repository.insert(company);
        List<CompanyMongoDB> companies = repository.findAll();

        Assertions.assertNotNull(
                companyStored.getId(),
                "Company id can not be null for the new inserted document" );

        Assertions.assertEquals(
                documentSize + 1,
                companies.size(),
                String.format("Number of companies stored in the collection must be equal to %d", documentSize + 1));
    }
}
