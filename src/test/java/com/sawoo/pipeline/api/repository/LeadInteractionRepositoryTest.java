package com.sawoo.pipeline.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.model.prospect.LeadInteraction;
import com.sawoo.pipeline.api.repository.interaction.LeadInteractionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LeadInteractionRepositoryTest {

    private static final File INTERACTION_JSON_DATA = Paths.get("src", "test", "resources", "test-data", "interaction-test-data.json").toFile();
    private static final List<Long> interactionIdList = new ArrayList<>();

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @Autowired
    private LeadInteractionRepository repository;

    @BeforeEach
    void beforeEach() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize our JSON file to an array of reviews
        LeadInteraction[] interactionList = mapper.readValue(INTERACTION_JSON_DATA, LeadInteraction[].class);

        // Load each entity into the dataStore
        Arrays.stream(interactionList).forEach((item) -> {
            interactionIdList.add(item.getKey().getId());
            datastoreTemplate.save(item);
        });
    }

    @AfterEach
    void afterEach() {
        // Drop the entity collection so we can start fresh
        datastoreTemplate.deleteAllById(interactionIdList, LeadInteraction.class);
        interactionIdList.clear();
    }
}
