package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.listener.SequenceStepCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SequenceEventListener extends AbstractMongoEventListener<Sequence> {

    private final SequenceStepCascadeOperationDelegator sequenceStepCascadeOperationDelegator;
    private final SequenceRepository repository;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Sequence> event) {
        Sequence sequence = event.getSource();
        List<SequenceStep> todos = Arrays.asList(sequence.getSteps().toArray(new SequenceStep[0]));
        sequence.getSteps().clear();
        todos.forEach(i -> sequenceStepCascadeOperationDelegator.onSave(i, sequence.getSteps()::add));
        super.onBeforeConvert(event);
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Sequence> event) {
        Document sequenceDoc = event.getSource();
        ObjectId sequenceId = sequenceDoc.get("_id", ObjectId.class);
        if (sequenceId != null) {
            repository.findById(sequenceId.toString())
                    .ifPresent(s -> s.getSteps().forEach(sequenceStepCascadeOperationDelegator::onDelete));
        }
        super.onBeforeDelete(event);
    }
}
