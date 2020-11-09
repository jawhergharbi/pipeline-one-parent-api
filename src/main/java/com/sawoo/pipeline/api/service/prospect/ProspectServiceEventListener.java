package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.dto.lead.ProspectDTO;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import org.springframework.stereotype.Component;

@Component
public class ProspectServiceEventListener implements BaseServiceEventListener<ProspectDTO, Prospect> {
    @Override
    public void onBeforeCreate(ProspectDTO dto, Prospect entity) {
        if (entity != null) {
            // Prospect salutation
            if (entity.getSalutation() == null) {
                entity.setSalutation(DomainConstants.SALUTATION_EMPTY);
            }

            // Consolidate fullName
            entity.setFullName(String.join(" ", dto.getFirstName(), dto.getLastName()));
        }
    }

    @Override
    public void onBeforeUpdate(ProspectDTO dto, Prospect entity) {
        if (entity != null) {
            // Consolidate firstName and lastName
            if (dto.getFirstName() != null || dto.getLastName() != null) {
                String firstName = dto.getFirstName() != null ? dto.getFirstName() : entity.getFirstName();
                String lastName = dto.getLastName() != null ? dto.getLastName() : entity.getLastName();
                entity.setFullName(String.join(" ", firstName, lastName));
            }
        }
    }
}
