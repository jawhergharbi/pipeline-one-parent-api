package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Primary
public class SequenceControllerDelegator extends BaseControllerDelegator<SequenceDTO, SequenceService> implements SequenceControllerUserDelegator, SequenceControllerAccountDelegator, SequenceControllerStepDelegator {

    private final SequenceControllerUserDelegator userDelegator;
    private final SequenceControllerAccountDelegator accountDelegator;
    private final SequenceControllerStepDelegator stepDelegator;

    @Autowired
    public SequenceControllerDelegator(
            SequenceService service,
            @Qualifier("sequenceUserController") SequenceControllerUserDelegator userDelegator,
            @Qualifier("sequenceAccountController") SequenceControllerAccountDelegator accountDelegator,
            @Qualifier("sequenceStepController") SequenceControllerStepDelegator stepDelegator) {
        super(service, ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI);
        this.userDelegator = userDelegator;
        this.accountDelegator = accountDelegator;
        this.stepDelegator = stepDelegator;
    }

    @Override
    public String getComponentId(SequenceDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<SequenceDTO> deleteUser(String id, String userId) throws ResourceNotFoundException, CommonServiceException {
        return userDelegator.deleteUser(id, userId);
    }

    @Override
    public ResponseEntity<List<SequenceDTO>> findByAccounts(Set<String> accountIds) throws CommonServiceException {
        return accountDelegator.findByAccounts(accountIds);
    }

    @Override
    public ResponseEntity<SequenceStepDTO> addStep(String sequenceId, SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return stepDelegator.addStep(sequenceId, step);
    }

    @Override
    public ResponseEntity<SequenceStepDTO> updateStep(String sequenceId, SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return stepDelegator.updateStep(sequenceId, step);
    }

    @Override
    public ResponseEntity<SequenceStepDTO> removeStep(String sequenceId, String sequenceStepId)
            throws ResourceNotFoundException {
        return stepDelegator.removeStep(sequenceId, sequenceStepId);
    }

    @Override
    public ResponseEntity<List<SequenceStepDTO>> getSteps(String sequenceId) throws ResourceNotFoundException {
        return stepDelegator.getSteps(sequenceId);
    }

    @Override
    public ResponseEntity<List<SequenceStepDTO>> getStepsByPersonality(String sequenceId, Integer personality) throws ResourceNotFoundException {
        return stepDelegator.getStepsByPersonality(sequenceId, personality);
    }
}
