package com.sawoo.pipeline.api.model.prospect;


import com.google.cloud.datastore.Key;
import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = DBConstants.LEAD_INTERACTION_DOCUMENT)
public class LeadInteractionOld {

    @Id
    private Key key;

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

    private LocalDateTime created;

    private LocalDateTime updated;

    @JMapConversion(from = {"key"}, to = {"id"})
    public Long conversion(Key key) {
        return key.getId();
    }
}
