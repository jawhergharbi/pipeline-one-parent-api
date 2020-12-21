package com.sawoo.pipeline.api.model.lead;


import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.LEAD_INTERACTION_DOCUMENT)
public class LeadInteraction extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    private Integer type;

    @JMap
    private Integer status;

    @JMap
    private UrlTitle invite;

    @JMap
    private Note note;

    @JMap
    private LocalDateTime scheduled;

    @DBRef(lazy = true)
    private Set<LeadInteraction> interactions;
    public Set<LeadInteraction> getInteractions() {
        if (interactions == null) {
            interactions = new HashSet<>();
        }
        return interactions;
    }
}
