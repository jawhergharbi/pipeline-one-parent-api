package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
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
public class LeadRepositoryTest {

    private static final File LEAD_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "lead-test-data.json").toFile();
    private static final List<Long> leadIdList = new ArrayList<>();
    private static final List<Long> interactionIdList = new ArrayList<>();
    private final Faker FAKER = Faker.instance();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private LeadRepository repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        Lead[] leadList = mapper.readValue(LEAD_JSON_DATA, Lead[].class);

        // Load each entity into the dataStore
        Arrays.stream(leadList).forEach((item) -> {
            leadIdList.add(item.getId());
            datastoreTemplate.save(item);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAllById(leadIdList, Lead.class);
        datastoreTemplate.deleteAllById(interactionIdList, LeadInteraction.class);
        leadIdList.clear();
        interactionIdList.clear();
    }

    @Test
    void findAllWhenTwoEntitiesFoundReturnsSuccess() {
        Iterable<Lead> entities = repository.findAll();

        Assertions.assertEquals(
                leadIdList.size(),
                (int) StreamSupport
                        .stream(entities.spliterator(), false).count(),
                String.format("Should be %d lead entities in the database", leadIdList.size()));
    }

    @Test
    void findByIdWhenEntityIdFoundReturnsSuccess() {
        Long LEAD_ID = 987L;
        Optional<Lead> entity = repository.findById(LEAD_ID);

        Assertions.assertTrue(entity.isPresent(), String.format("Lead with [id]: %s can not be null", LEAD_ID));
        Assertions.assertEquals(LEAD_ID, entity.get().getId(), String.format("Lead [id] must be %s", LEAD_ID));
    }

    @Test
    void findByFullNameWhenEntityIdFoundReturnsSuccess() {
        String LEAD_FULL_NAME = "Miguel Miguelito";
        Optional<Lead> entity = repository.findByFullName(LEAD_FULL_NAME);

        Assertions.assertTrue(entity.isPresent(), String.format("Lead with [name]: %s can not be null", LEAD_FULL_NAME));
        Assertions.assertEquals(LEAD_FULL_NAME, entity.get().getFullName(), String.format("Lead [name] must be %s", LEAD_FULL_NAME));
    }

    @Test
    void findByIdWhenEntityNotFoundReturnsSuccess() {
        Long LEAD_ID = 982327L;
        Optional<Lead> entity = repository.findById(LEAD_ID);

        Assertions.assertFalse(entity.isPresent(), String.format("Lead with [id]: %s can be found", LEAD_ID));
    }

    @Test
    void saveWhenCreateNewEntityReturnsSuccess() {
        String LEAD_FULL_NAME = FAKER.name().fullName();
        String LEAD_LINKED_IN_URL = FAKER.internet().url();
        String LEAD_LINKED_THREAD_URL = FAKER.internet().url();
        Lead mockedEntity = newMockedEntity(LEAD_FULL_NAME, LEAD_LINKED_IN_URL, LEAD_LINKED_THREAD_URL);

        repository.save(mockedEntity);
        leadIdList.add(mockedEntity.getId());

        Iterable<Lead> leads = repository.findAll();
        Assertions.assertEquals(
                leadIdList.size(),
                (int) StreamSupport
                        .stream(leads.spliterator(), false).count(),
                String.format("Should be [%d] lead entity in the database", leadIdList.size()));
        Assertions.assertNotNull(mockedEntity.getId(), "Lead [id] can not be null after being saved");
        Assertions.assertNotNull(mockedEntity.getCompany().getId(), "Company id has been assigned");
    }


    private Lead newMockedEntity(String fullName, String linkedInUrl, String linkedInThread) {
        Lead mockedEntity = new Lead();
        mockedEntity.setFullName(fullName);
        mockedEntity.setLinkedInUrl(linkedInUrl);
        mockedEntity.setLinkedInThread(linkedInThread);
        mockedEntity.setEmail(FAKER.internet().emailAddress());
        mockedEntity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedEntity.setPosition(FAKER.company().profession());
        mockedEntity.setCompany(Company
                .builder()
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .build());
        return mockedEntity;
    }
}
