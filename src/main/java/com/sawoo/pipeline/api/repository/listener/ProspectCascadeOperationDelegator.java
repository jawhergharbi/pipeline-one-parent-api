package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ProspectCascadeOperationDelegator implements CascadeOperationDelegation<Prospect> {

    private final ProspectRepository prospectRepository;

    @Override
    public void onSave(Prospect child, Consumer<Prospect> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                prospectRepository
                        .findByLinkedInUrl(child.getLinkedInUrl())
                        .ifPresentOrElse(parentFunction,
                                () -> {
                                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                    child.setCreated(now);
                                    child.setUpdated(now);
                                    prospectRepository.insert(child);
                                });
            } else {
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                child.setUpdated(now);
                prospectRepository.save(child);
            }
        }
    }
}
