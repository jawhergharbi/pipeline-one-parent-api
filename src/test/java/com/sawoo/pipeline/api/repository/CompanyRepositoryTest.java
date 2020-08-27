package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.model.Company;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
public class CompanyRepositoryTest {

    private static final File COMPANY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "company-test-data.json").toFile();
    private static final List<Long> companyIdList = new ArrayList<>();
    private final Faker FAKER = Faker.instance();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private CompanyRepository repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Company[] companyList = mapper.readValue(COMPANY_JSON_DATA, Company[].class);

        // Load each auth entity into the dataStore
        Arrays.stream(companyList).forEach((company) -> {
            companyIdList.add(company.getId());
            datastoreTemplate.save(company);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAll(Company.class);
        companyIdList.clear();
    }

    @Test
    void findAllWhenTwoEntitiesFoundReturnsSuccess() {
        Iterable<Company> auths = repository.findAll();

        Assertions.assertEquals(
                companyIdList.size(),
                (int) StreamSupport
                        .stream(auths.spliterator(), true).count(),
                String.format("Should be %d Company entities in the database", companyIdList.size()));
    }

    @Test
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        Long COMPANY_ID = 123L;
        Optional<Company> entity = repository.findById(COMPANY_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("Company with [id]: %s can not be null", COMPANY_ID));
        Assertions.assertEquals(COMPANY_ID, entity.get().getId(), String.format("Company [id] must be %s", COMPANY_ID));
    }

    @Test
    void findByNameWhenEntityIdFoundReturnsSuccess() {
        String COMPANY_NAME = "company_name_1";
        Optional<Company> entity = repository.findByName(COMPANY_NAME);

        Assertions.assertTrue(entity.isPresent(), String.format("Company with [name]: %s can not be null", COMPANY_NAME));
        Assertions.assertEquals(COMPANY_NAME, entity.get().getName(), String.format("Company [name] must be %s", COMPANY_NAME));
    }

    @Test
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        Long COMPANY_ID = 12345L;
        Optional<Company> entity = repository.findById(COMPANY_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("Company with [id]: %s can be found", COMPANY_ID));
    }

    @Test
    void saveWhenAddNewEntityReturnsSuccess() {
        Company company = new Company();
        company.setName(FAKER.company().name());
        company.setUrl(FAKER.company().url());

        repository.save(company);
        companyIdList.add(company.getId());

        Iterable<Company> companies = repository.findAll();
        Assertions.assertEquals(
                companyIdList.size(),
                (int) StreamSupport
                        .stream(companies.spliterator(), true).count(),
                String.format("Should be [%d] company entities in the database", companyIdList.size()) );
        Assertions.assertNotNull(company.getId(), "Company [id] can not be null after being saved");
    }
}
