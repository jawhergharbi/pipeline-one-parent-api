package com.sawoo.pipeline.api.integration.person;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.integration.MongoDataFile;
import com.sawoo.pipeline.api.integration.MongoSpringExtension;
import com.sawoo.pipeline.api.integration.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.mock.PersonMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.person.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@MongoDataFile(
        value = "person-integration-test-data.json",
        classType = Person.class,
        collectionNames = {DBConstants.PERSON_DOCUMENT})
public class PersonIntegrationTest extends BaseIntegrationTest<PersonDTO, Person, PersonMockFactory> {

    private static final String PERSON_INTEGRATION_EXPECTED_RESULTS_FILE_NAME = "person-integration-expected-results.json";

    @Autowired
    public PersonIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, PersonMockFactory mockFactory) {
        super(mockMvc, mongoTemplate,
                ControllerConstants.PERSON_CONTROLLER_API_BASE_URI,
                DBConstants.PERSON_DOCUMENT,
                PERSON_INTEGRATION_EXPECTED_RESULTS_FILE_NAME,
                mockFactory);
    }

    @Override
    protected Class<Person> getClazz() {
        return Person.class;
    }
}
