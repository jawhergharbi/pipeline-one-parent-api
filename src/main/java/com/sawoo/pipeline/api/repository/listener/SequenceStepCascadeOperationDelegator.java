package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.sequence.SequenceStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class SequenceStepCascadeOperationDelegator implements CascadeOperationDelegation<SequenceStep> {

    private final SequenceStepRepository repository;

    @Override
    public void onSave(SequenceStep child, Consumer<SequenceStep> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                SequenceStep step = repository.insert(child);
                parentFunction.accept(step);
            } else {
                Optional<SequenceStep> step = repository.findById(child.getId());
                step.ifPresent(parentFunction);
            }
        }
    }

    @Override
    public void onDelete(SequenceStep child) {
        if (child != null) {
            repository.delete(child);
        }
    }
}
