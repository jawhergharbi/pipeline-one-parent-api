package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.sequencestep.SequenceStepMapper;
import com.sawoo.pipeline.api.service.sequencestep.SequenceStepService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SequenceStepsServiceTest extends BaseLightServiceTest<SequenceDTO, Sequence, SequenceRepository, SequenceService, SequenceMockFactory> {

    @MockBean
    private SequenceRepository repository;

    @MockBean
    private SequenceStepService sequenceStepService;

    @Autowired
    public SequenceStepsServiceTest(SequenceMockFactory mockFactory, SequenceService service) {
        super(mockFactory, DBConstants.SEQUENCE_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("addStep: add step to sequence - Success")
    void addStepWhenStepValidReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEP_SIZE = 4;
        Sequence mockedEntity = newSequenceWithSteps(SEQUENCE_ID, STEP_SIZE);
        SequenceStepDTO mockedStep = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        mockedStep.setPosition(STEP_SIZE + 1);
        String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
        SequenceStepDTO savedStepEntity = getMockFactory().getSequenceStepMockFactory().newDTO(SEQUENCE_STEP_ID, mockedStep);

        // Set up the mocked repository and services
        doReturn(Optional.of(mockedEntity)).when(getService().getRepository()).findById(anyString());
        doReturn(savedStepEntity).when(sequenceStepService).create(mockedStep);
        doReturn(mockedEntity).when(getService().getRepository()).save(any(Sequence.class));
        doReturn(new SequenceStepMapper()).when(sequenceStepService).getMapper();

        // Execute the service call
        SequenceStepDTO step = getService().addStep(SEQUENCE_ID, mockedStep);

        // Assertions
        Assertions.assertAll(String.format("Add step for sequence id [%s]", SEQUENCE_ID),
                () -> Assertions.assertNotNull(step, "Step can not be null"),
                () -> Assertions.assertEquals(
                        SEQUENCE_STEP_ID,
                        step.getId(),
                        String.format("Step id must be [%s]", SEQUENCE_STEP_ID)),
                () -> Assertions.assertEquals(
                        STEP_SIZE + 1,
                        mockedEntity.getSteps().size(),
                        String.format("Step size must be [%d]", STEP_SIZE + 1)));

        verify(getService().getRepository(), times(1)).findById(anyString());
        verify(getService().getRepository(), times(1)).save(any(Sequence.class));
    }

    @Test
    @DisplayName("addStep: add step to sequence - Failure")
    void addStepWhenStepPositionAndPersonalityIsInTheSequenceReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEP_SIZE = 4;
        Sequence mockedEntity = newSequenceWithSteps(SEQUENCE_ID, STEP_SIZE);
        SequenceStepDTO mockedStep = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        mockedStep.setPosition(STEP_SIZE);

        // Set up the mocked repository and services
        doReturn(Optional.of(mockedEntity)).when(getService().getRepository()).findById(anyString());

        SequenceService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.addStep(SEQUENCE_ID, mockedStep),
                "addStep must throw CommonServiceException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.SEQUENCE_STEP_ADD_STEP_POSITION_AND_PERSONALITY_ALREADY_FILLED_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(3, exception.getArgs().length);

        verify(service.getRepository(), times(1)).findById(anyString());
        verify(sequenceStepService, never()).create(any(SequenceStepDTO.class));
    }

    @Test
    @DisplayName("addStep: add step to sequence when sequence not found - Failure")
    void addStepWhenSequenceNotFoundReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEP_SIZE = 4;
        SequenceStepDTO mockedStep = getMockFactory().getSequenceStepMockFactory().newDTO(null);
        mockedStep.setPosition(STEP_SIZE);

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(getService().getRepository()).findById(anyString());

        // Execute the service
        SequenceService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addStep(SEQUENCE_ID, mockedStep),
                "addStep must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(service.getRepository(), times(1)).findById(anyString());
        verify(sequenceStepService, never()).create(any(SequenceStepDTO.class));
    }

    @Test
    @DisplayName("getSteps:sequence not found - Failure")
    void getStepsWhenSequenceNotFoundReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(getService().getRepository()).findById(anyString());

        // Execute the service
        SequenceService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.getSteps(SEQUENCE_ID),
                "getSteps must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(service.getRepository(), times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getSteps: sequence found - Success")
    void getStepsWhenSequenceNotFoundReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEPS_SIZE = 3;
        Sequence mockedSequence = newSequenceWithSteps(SEQUENCE_ID, STEPS_SIZE);

        // Set up the mocked repository and services
        doReturn(Optional.of(mockedSequence)).when(getService().getRepository()).findById(anyString());
        doReturn(new SequenceStepMapper()).when(sequenceStepService).getMapper();

        // Execute the service
        List<SequenceStepDTO> steps = getService().getSteps(SEQUENCE_ID);

        // Assertions
        Assertions.assertFalse(steps.isEmpty(), "Step list can not be empty");
        Assertions.assertEquals(STEPS_SIZE, steps.size(), String.format("Step list size must be [%d]", STEPS_SIZE));

        verify(getService().getRepository(), times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getStepsByPersonality: sequence found - Success")
    void getStepsByPersonalityWhenSequenceNotFoundReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        int STEPS_SIZE = 3;
        int PERSONALITY_OUTGOING = 2;
        Sequence mockedSequence = newSequenceWithSteps(SEQUENCE_ID, STEPS_SIZE);
        SequenceStep stepNoPersonality = getMockFactory()
                .getSequenceStepMockFactory()
                .newSequenceStepEntity(getMockFactory().getFAKER().internet().uuid(), STEPS_SIZE + 1);
        stepNoPersonality.setPersonality(null);
        mockedSequence.getSteps().add(stepNoPersonality);
        SequenceStep stepPersonalityOutgoing = getMockFactory()
                .getSequenceStepMockFactory()
                .newSequenceStepEntity(getMockFactory().getFAKER().internet().uuid(), STEPS_SIZE + 1);
        stepPersonalityOutgoing.setPersonality(PERSONALITY_OUTGOING);
        mockedSequence.getSteps().add(stepPersonalityOutgoing);

        // Set up the mocked repository and services
        doReturn(Optional.of(mockedSequence)).when(getService().getRepository()).findById(anyString());
        doReturn(new SequenceStepMapper()).when(sequenceStepService).getMapper();

        // Execute the service
        List<SequenceStepDTO> steps = getService().getStepsByPersonality(SEQUENCE_ID, PERSONALITY_OUTGOING);

        // Assertions
        Assertions.assertFalse(steps.isEmpty(), "Step list can not be empty");
        Assertions.assertEquals(2, steps.size(), String.format("Step list size must be [%d]", 2));

        verify(getService().getRepository(), times(1)).findById(anyString());
    }


    private Sequence newSequenceWithSteps(String sequenceId, int stepsNumber) {
        Sequence mockEntity = getMockFactory().newEntity(sequenceId);
        List<SequenceStep> steps = IntStream.range(0, stepsNumber).mapToObj( (idx) -> {
           String SEQUENCE_STEP_ID = getMockFactory().getSequenceStepMockFactory().getComponentId();
           return getMockFactory().getSequenceStepMockFactory().newSequenceStepEntity(SEQUENCE_STEP_ID, idx + 1);
        }).collect(Collectors.toList());
        mockEntity.setSteps(steps);
        return mockEntity;
    }
}
