package com.sawoo.pipeline.api.model.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@SuperBuilder
public class CampaignLead extends BaseEntity {

    @DBRef(lazy = true)
    private Lead lead;

    @DBRef(lazy = true)
    private Sequence sequence;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;

    @JMap
    private CampaignLeadStatus status;

    @JMapConversion(from = {"status"}, to = {"status"})
    public CampaignLeadStatus statusConversion(Integer status) {
        return CampaignLeadStatus.fromValue(status);
    }
}
