package com.sawoo.pipeline.api.model.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CampaignLead extends BaseEntity {

    @JMap
    @DBRef(lazy = true)
    private Lead lead;

    @JMap
    @DBRef(lazy = true)
    private Sequence sequence;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;


}
