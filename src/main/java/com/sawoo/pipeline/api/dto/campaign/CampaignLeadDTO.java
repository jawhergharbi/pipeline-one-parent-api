package com.sawoo.pipeline.api.dto.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class CampaignLeadDTO extends BaseEntityDTO {

    @JMap
    private LeadDTO lead;

    @JMap
    private SequenceBaseDTO sequence;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;
}
