package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class ProspectServiceEventListenerTest {

    @Autowired
    private ProspectServiceEventListener listener;

    @Autowired
    private ProspectMockFactory mockFactory;

    @Autowired
    private ProspectMapper mapper;

    @Test
    @DisplayName("onBeforeInsert: prospect qualification is initialized - Success")
    void onBeforeInsertWhenSequenceContainsUsersWithoutTimestampsReturnsSuccess() {
        // Set up mocked entities
        ProspectDTO postDTO = mockFactory.newDTO(null);
        int DEFAULT_QUALIFICATION = ProspectQualification.TARGETABLE.getValue();
        Prospect entity = mapper.getMapperIn().getDestination(postDTO);
        entity.setQualification(null);

        // Execute the service call
        listener.handleBeforeInsertEvent(
                new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getQualification(), "Qualification can not be null");
        Assertions.assertEquals(
                DEFAULT_QUALIFICATION,
                entity.getQualification().getValue(),
                String.format("Qualification must be equals to [%d]", DEFAULT_QUALIFICATION));
    }
}
