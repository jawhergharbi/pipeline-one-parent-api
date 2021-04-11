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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    // TODO check whether this can be done diffeently. Steps are deleted before the sequence. If we do it after the sequence is deleted
    // we would not find the steps to be deleted
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Sequence> event) {
        Document sequenceDoc = event.getSource();
        List<String> sequenceIds = new ArrayList<>();
        try {
            ObjectId objId = sequenceDoc.getObjectId("_id");
            sequenceIds.add(objId.toString());
        } catch (ClassCastException err) {
            Document docId = sequenceDoc.get("_id", Document.class);
            List<ObjectId> objIds = docId.getList("$in", ObjectId.class);
            sequenceIds = objIds.stream().map(ObjectId::toString).collect(Collectors.toList());
        }

        if (!sequenceIds.isEmpty()) {
            sequenceIds.forEach(sequenceId -> repository.findById(sequenceId).ifPresent(s -> s.getSteps().forEach(sequenceStepCascadeOperationDelegator::onDelete)));
        }
        super.onBeforeDelete(event);
    }
}
