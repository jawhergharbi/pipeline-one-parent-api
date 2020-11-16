package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class LeadEventListener extends AbstractMongoEventListener<Lead> {

    private final ProspectRepository prospectRepository;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Lead> event) {
        Lead lead = event.getSource();
        Prospect prospect = lead.getProspect();
        if (prospect != null) {
            if (prospect.getId() == null) {
                prospectRepository.
                        findByLinkedInUrl(prospect.getLinkedInUrl())
                        .ifPresentOrElse(
                                lead::setProspect,
                                () -> {
                                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                    prospect.setCreated(now);
                                    prospect.setUpdated(now);
                                    prospectRepository.insert(prospect);
                                });
            } else {
                prospectRepository
                        .findById(prospect.getId())
                        .ifPresentOrElse(
                                lead::setProspect,
                                () -> {
                                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                    prospect.setUpdated(now);
                                    prospectRepository.save(prospect);
                                });
            }
        }
        super.onBeforeConvert(event);
    }
}
