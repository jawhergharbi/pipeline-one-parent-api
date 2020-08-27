package com.sawoo.pipeline.api.model.lead;


import com.google.cloud.datastore.Key;
import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = DataStoreConstants.LEAD_INTERACTION_ENTITY_NAME)
public class LeadInteraction {

    @Id
    private Key key;

    @JMap
    private Integer type;

    @JMap
    private Integer status;

    @JMap
    @Field(name = "url_invite")
    private UrlTitle urlInvite;

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
