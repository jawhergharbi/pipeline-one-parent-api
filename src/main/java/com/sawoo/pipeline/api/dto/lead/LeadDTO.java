package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import lombok.*;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class LeadDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    private ProspectDTO prospect;

    @JMap
    @Field(name = "linked_in_thread")
    private String linkedInThread;

    @JMap
    @Field(name = "lead_notes")
    private Note leadNotes;

    @JMap
    @Field(name = "company_notes")
    private Note companyNotes;

    @JMap
    private Status status;
}
