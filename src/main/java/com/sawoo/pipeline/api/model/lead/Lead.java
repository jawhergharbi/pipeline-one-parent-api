package com.sawoo.pipeline.api.model.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.LEAD_DOCUMENT)
public class Lead extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    @DBRef
    private Prospect prospect;

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
