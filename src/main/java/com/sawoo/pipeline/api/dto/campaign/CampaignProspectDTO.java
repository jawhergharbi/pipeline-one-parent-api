package com.sawoo.pipeline.api.dto.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceBaseDTO;
import com.sawoo.pipeline.api.model.campaign.CampaignProspectStatus;
import com.sawoo.pipeline.api.model.sequence.Sequence;
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
public class CampaignProspectDTO extends BaseEntityDTO {

    @JMap
    private ProspectDTO prospect;

    @JMap
    private SequenceBaseDTO sequence;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;

    @JMap
    private Integer status;

    @JMapConversion(from = {"sequence"}, to = {"sequence"})
    public SequenceBaseDTO sequenceConversion(Sequence sequence) {
        return SequenceBaseDTO.builder()
                .id(sequence.getId())
                .componentId(sequence.getComponentId())
                .created(sequence.getCreated())
                .updated(sequence.getUpdated())
                .name(sequence.getName())
                .description(sequence.getDescription())
                .build();
    }

    @JMapConversion(from = {"status"}, to = {"status"})
    public Integer statusConversion(CampaignProspectStatus status) {
        return status != null ? status.getValue() : -1;
    }
}
