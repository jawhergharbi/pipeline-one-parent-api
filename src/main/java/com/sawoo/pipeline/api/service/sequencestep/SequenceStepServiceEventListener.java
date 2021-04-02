package com.sawoo.pipeline.api.service.sequencestep;

import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.common.Link;
import com.sawoo.pipeline.api.model.common.LinkType;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SequenceStepServiceEventListener {

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<SequenceStepDTO, SequenceStep> event) {
        log.debug("Sequence Step before insert listener");
        SequenceStep entity = event.getModel();

        // Attachment init
        Link attachment = entity.getAttachment();
        if (attachment != null && attachment.getType() == null) {
            attachment.setType(LinkType.ATTACHMENT);
        }
    }
}
