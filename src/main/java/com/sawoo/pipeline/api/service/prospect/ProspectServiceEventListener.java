package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class ProspectServiceEventListener {

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<ProspectDTO, Prospect> event) {
        log.debug("Prospect before save listener");
        Prospect entity = event.getModel();
        ProspectDTO dto = event.getDto();
        // Add default status
        if (entity.getStatus() == null) {
            entity.setStatus(Status.builder()
                    .value(ProspectQualification.TARGETABLE.getValue())
                    .updated(LocalDateTime.now(ZoneOffset.UTC))
                    .build());
        }
    }
}
