package com.sawoo.pipeline.api.repository.company;

import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class CompanyRepositoryTest extends BaseRepositoryTest<Company, CompanyRepository, CompanyMockFactory> {

    private static final String TEST_JSON_DATA_FILE_NAME = "company-test-data.json";
    private static final String ENTITY_ID = "6072a9d36605fb3ba7f432ee";

    @Autowired
    public CompanyRepositoryTest(CompanyRepository repository, CompanyMockFactory mockFactory) {
        super(repository, TEST_JSON_DATA_FILE_NAME, ENTITY_ID, Company.class.getSimpleName(), mockFactory);
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
        String COMPANY_NAME  = getMockFactory().getFAKER().company().name();
        String COMPANY_URL  = getMockFactory().getFAKER().company().url();
        return getMockFactory().newEntity(COMPANY_NAME, COMPANY_URL);
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
