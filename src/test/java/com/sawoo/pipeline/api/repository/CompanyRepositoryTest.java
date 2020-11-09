package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.Company;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data"), @Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class CompanyRepositoryTest extends BaseRepositoryTest<Company, CompanyRepository> {

    private static final File COMPANY_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "company-test-data.json").toFile();
    private static final String COMPANY_ID = "5fa3ce63ee4ef64d966da45b";

    @Autowired
    public CompanyRepositoryTest(CompanyRepository repository) {
        super(repository, COMPANY_JSON_DATA, COMPANY_ID, Company.class.getSimpleName());
    }

    @Override
    protected Class<Company[]> getClazz() {
        return Company[].class;
    }

    @Override
    protected String getComponentId(Company component) {
        return component.getId();
    }

    @Override
    protected Company getNewEntity() {
        return getMockFactory()
                .newCompanyEntity(FAKER.company().name(), FAKER.company().url());
    }

    @Test
    @DisplayName("findByName: entity found - Success")
    void findByNameWhenEntityIdFoundReturnsSuccess() {
        String COMPANY_NAME = "google";
        Optional<Company> entity = getRepository().findByName(COMPANY_NAME);

        Assertions.assertTrue(entity.isPresent(), String.format("Company with [name]: %s can not be null", COMPANY_NAME));
        Assertions.assertEquals(COMPANY_NAME, entity.get().getName(), String.format("Company [name] must be %s", COMPANY_NAME));
    }

    @Test
    @DisplayName("findByName: entity not found - Failure")
    void findByIdWhenEntityNotFoundReturnsFailure() {
        String COMPANY_ID = "wrong_id";
        Optional<Company> entity = getRepository().findById(COMPANY_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("Company with [id]: %s can not be found", COMPANY_ID));
    }
}
