package com.sawoo.pipeline.api.dto.campaign;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
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
public class CampaignDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    private String name;

    @JMap
    private String description;

    @JMap
    private LocalDateTime startDate;

    @JMap
    private LocalDateTime endDate;

    @JMap
    private CampaignStatus status;

    @JMap
    private String componentId;

    @JMap
    private LocalDateTime actualStartDate;

    @JMap
    private LocalDateTime actualEndDate;
}
