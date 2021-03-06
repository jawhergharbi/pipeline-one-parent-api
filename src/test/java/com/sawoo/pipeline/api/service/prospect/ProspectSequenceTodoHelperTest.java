package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.mock.SequenceStepMockFactory;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.model.todo.TodoType;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class ProspectSequenceTodoHelperTest {

    @Getter
    private final ProspectMockFactory prospectMockFactory;

    @Getter
    private final SequenceStepMockFactory sequenceStepMockFactory;

    @Getter
    private final ProspectSequenceTodoHelper sequenceTodoHelper;

    @Autowired
    public ProspectSequenceTodoHelperTest(ProspectMockFactory prospectMockFactory, SequenceStepMockFactory sequenceStepMockFactory, ProspectSequenceTodoHelper sequenceTodoHelper) {
        this.prospectMockFactory = prospectMockFactory;
        this.sequenceStepMockFactory = sequenceStepMockFactory;
        this.sequenceTodoHelper = sequenceTodoHelper;
    }

    @Test
    @DisplayName("mapSequenceStepToTODO: everything is correctly informed - Success")
    void mapSequenceStepToTODOWhenEverythingIsCorrectlyInformedReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getProspectMockFactory().getComponentId();
        String STEP_ID = getSequenceStepMockFactory().getComponentId();
        String SEQUENCE_ID = getProspectMockFactory().getFAKER().internet().uuid();
        Prospect mockProspect = getProspectMockFactory().newEntity(PROSPECT_ID);
        SequenceStepDTO mockSequenceStep = getSequenceStepMockFactory().newDTO(STEP_ID);
        mockSequenceStep.setTimespan(0);
        UserCommon user = UserCommon.builder()
                .fullName(getProspectMockFactory().getFAKER().name().fullName())
                .id(getProspectMockFactory().getFAKER().internet().uuid())
                .type(UserCommonType.PROSPECT)
                .build();
        LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC);

        // Execute the call
        TodoAssigneeDTO todo = sequenceTodoHelper.mapSequenceStepToTODO(mockSequenceStep, user, mockProspect, SEQUENCE_ID, startDate);

        // Assertions
        Assertions.assertTrue(LocalDate.now().isEqual(todo.getScheduled().toLocalDate()), "Schedule date must be today");
    }

    @Test
    @DisplayName("mapSequenceStepToTODO: todo Type should match default value 0  - Success")
    void mapSequenceStepToTODOWhenTypeDefaultValueIsOutGoingAndReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getProspectMockFactory().getComponentId();
        String STEP_ID = getSequenceStepMockFactory().getComponentId();
        String SEQUENCE_ID = getProspectMockFactory().getFAKER().internet().uuid();
        Prospect mockProspect = getProspectMockFactory().newEntity(PROSPECT_ID);
        SequenceStepDTO mockSequenceStep = getSequenceStepMockFactory().newDTO(STEP_ID);
        UserCommon user = UserCommon.builder()
                .fullName(getProspectMockFactory().getFAKER().name().fullName())
                .id(getProspectMockFactory().getFAKER().internet().uuid())
                .type(UserCommonType.PROSPECT)
                .build();
        LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC);

        // Execute the call
        TodoAssigneeDTO todo = sequenceTodoHelper.mapSequenceStepToTODO(mockSequenceStep, user, mockProspect, SEQUENCE_ID, startDate);

        // Assertions
        Assertions.assertEquals(
                TodoType.OUT_GOING_INTERACTION,
                todo.getType(),
                String.format("Todo property type must be initialized with the default value %s", TodoType.OUT_GOING_INTERACTION));
        Assertions.assertEquals(
                TodoSourceType.AUTOMATIC,
                todo.getSource().getType(),
                String.format("Source type should match default value %s", TodoSourceType.AUTOMATIC));
        Assertions.assertEquals(
                TodoStatus.PENDING.getValue(),
                todo.getStatus(),
                String.format("Status should match default value %s", TodoStatus.PENDING.getValue()));
    }
}
