package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.listener.SequenceStepCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SequenceEventListener extends AbstractMongoEventListener<Sequence> {

    private final SequenceStepCascadeOperationDelegator sequenceStepCascadeOperationDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Sequence> event) {
        Sequence sequence = event.getSource();
        List<SequenceStep> interactions = Arrays.asList(sequence.getSteps().toArray(new SequenceStep[0]));
        sequence.getSteps().clear();
        interactions.forEach(i -> sequenceStepCascadeOperationDelegator.onSave(i, sequence.getSteps()::add));
        super.onBeforeConvert(event);
    }
}
